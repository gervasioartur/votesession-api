package com.votesession.api.agennda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votesession.api.dto.*;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.exception.BusinessException;
import com.votesession.domain.exception.ConflictException;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.mocks.MocksFactory;
import com.votesession.service.contracts.AgendaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AgendaControllerTests {
    private final String URL = "/agendas";

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ModelMapper mapper;

    @MockBean
    private MockMvc mvc;

    @MockBean
    private AgendaService service;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("Should return 500 if an unexpected exception is thrown")
    void shouldReturn500IfUnexpectedExceptionIsThrown() throws Exception {
        CreateAgendaRequest requestParams = MocksFactory.createAgendaRequestFactory();
        Agenda agenda = MocksFactory.agendaWithNoIdFactory(requestParams);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.mapper.map(requestParams, Agenda.class)).thenReturn(agenda);
        Mockito.when(this.service.create(agenda)).thenThrow(RuntimeException.class);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("body",
                        Matchers.is("An unexpected error occurred. Please try again later.")));

        Mockito.verify(this.mapper, Mockito.times(1)).map(requestParams, Agenda.class);
        Mockito.verify(this.service, Mockito.times(1)).create(agenda);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 if title is empty or null empty")
    void shouldReturn400IfTitleIsEmptyOrNullOrEmpty(String title) throws Exception {
        CreateAgendaRequest requestParams = new CreateAgendaRequest(title, "any_description");
        Agenda agenda = MocksFactory.agendaWithNoIdFactory(requestParams);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("body",
                        Matchers.is("Title is required.")));

        Mockito.verify(this.mapper, Mockito.times(0)).map(requestParams, Agenda.class);
        Mockito.verify(this.service, Mockito.times(0)).create(agenda);
    }

    @Test
    @DisplayName("Should return 201 on create agenda success")
    void shouldReturn201OnCreateAgendaSuccess() throws Exception {
        CreateAgendaRequest requestParams = MocksFactory.createAgendaRequestFactory();
        Agenda agenda = MocksFactory.agendaWithNoIdFactory(requestParams);
        Agenda savedAgenda = MocksFactory.agendaWithIdFactory(agenda);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.mapper.map(requestParams, Agenda.class)).thenReturn(agenda);
        Mockito.when(this.service.create(agenda)).thenReturn(savedAgenda);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("body",
                        Matchers.is("Created.")));

        Mockito.verify(this.mapper, Mockito.times(1)).map(requestParams, Agenda.class);
        Mockito.verify(this.service, Mockito.times(1)).create(agenda);
    }

    @Test
    @DisplayName("Should return 200 on read all agendas success")
    void shouldReturn200OnReadAllAgendasSuccess() throws Exception {
        List<Agenda> agendas = List.of(MocksFactory.agendaWithIdFactory(), MocksFactory.agendaWithIdFactory());

        Mockito.when(this.service.readAll()).thenReturn(agendas);
        Mockito.when(this.mapper.map(Mockito.any(Agenda.class), Mockito.eq(AgendaResponse.class))).thenAnswer(invocation -> {
            Agenda agenda = invocation.getArgument(0);
            List<VotingSessionResponse> votingSessionsResponse = agenda.getVotingSessions()
                    .stream()
                    .map(votingSession -> new VotingSessionResponse(
                            votingSession.getId(),
                            votingSession.getStartDate(),
                            votingSession.getEndDate()))
                    .toList();
            return new AgendaResponse(agenda.getId(), agenda.getTitle(), agenda.getDescription(), votingSessionsResponse);
        });
        ;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(this.URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk());

        Mockito.verify(this.service, Mockito.times(1)).readAll();
        Mockito.verify(this.mapper, Mockito.times(agendas.size())).map(Mockito.any(Agenda.class),
                Mockito.eq(AgendaResponse.class));
    }

    @Test
    @DisplayName("Should return 404 if NotFoundException is thrown on open session")
    void shouldReturn404IfNotFoundExceptionIsThrownOnOpenSession() throws Exception {
        long agendaId = MocksFactory.faker.number().randomNumber();
        OpenVotingSessionRequest requestParams = OpenVotingSessionRequest
                .builder()
                .duration((int) MocksFactory.faker.number().randomNumber())
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.service.openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration())))
                .thenThrow(new NotFoundException("Could not find agenda with id " + agendaId));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + agendaId + "/session")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("body",
                        Matchers.is("Could not find agenda with id " + agendaId)));

        Mockito.verify(this.service, Mockito.times(1))
                .openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration()));
    }

    @Test
    @DisplayName("Should return 400 if duration is less than 0")
    void shouldReturn400IfDurationIsLessThan0() throws Exception {
        long agendaId = MocksFactory.faker.number().randomNumber();
        OpenVotingSessionRequest requestParams = OpenVotingSessionRequest
                .builder()
                .duration(-1)
                .build();

        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(requestParams.getDuration());
        votingSession.setId(agendaId);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.service.openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration())))
                .thenReturn(votingSession);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + agendaId + "/session")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("body",
                        Matchers.is("The duration must be equal to or greater than zero.")));

        Mockito.verify(this.service, Mockito.times(0))
                .openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration()));
    }

    @Test
    @DisplayName("Should return 200 on open session success")
    void shouldReturn200OnOpenSessionSuccess() throws Exception {
        long agendaId = MocksFactory.faker.number().randomNumber();
        OpenVotingSessionRequest requestParams = OpenVotingSessionRequest
                .builder()
                .duration((int) MocksFactory.faker.number().randomNumber())
                .build();

        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(requestParams.getDuration());
        votingSession.setId(agendaId);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.service.openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration())))
                .thenReturn(votingSession);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at time' HH:mm");
        String formattedStartDate = votingSession.getStartDate().format(formatter);
        String formattedEndDate = votingSession.getEndDate().format(formatter);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + agendaId + "/session")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("body",
                        Matchers.is("Voting opened successfully, it starts on  "
                                + formattedStartDate + " and ends on  " + formattedEndDate)));

        Mockito.verify(this.service, Mockito.times(1))
                .openSession(Mockito.any(VotingSession.class), Mockito.eq(requestParams.getDuration()));
    }

    @Test
    @DisplayName("Should return 409 if conflictException is thrown on save user vote")
    void shouldReturn409IfConflictExceptionIsThrownOnSaveUserVote() throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(MocksFactory.faker.number().randomNumber())
                .vote("Não")
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.doThrow(new ConflictException("User already voted."))
                .when(this.service)
                .vote(Mockito.any(Vote.class));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("body",
                        Matchers.is("User already voted.")));

        Mockito.verify(this.service, Mockito.times(1))
                .vote(Mockito.any(Vote.class));
    }

    @Test
    @DisplayName("Should return 404 if NotFoundException is thrown on save user vote")
    void shouldReturn409ConflictExceptionIsThrownOnSaveUserVote() throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(MocksFactory.faker.number().randomNumber())
                .vote("Não")
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.doThrow(new NotFoundException("Unable to find agenda with id : " + requestParams.getAgendaId()))
                .when(this.service)
                .vote(Mockito.any(Vote.class));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("body",
                        Matchers.is("Unable to find agenda with id : " + requestParams.getAgendaId())));

        Mockito.verify(this.service, Mockito.times(1))
                .vote(Mockito.any(Vote.class));
    }

    @Test
    @DisplayName("Should return 400 if BusinessException is thrown on save user vote")
    void shouldReturn400BusinessExceptionIsThrownOnSaveUserVote() throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(MocksFactory.faker.number().randomNumber())
                .vote("Não")
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.doThrow(new BusinessException("User unable to vote."))
                .when(this.service)
                .vote(Mockito.any(Vote.class));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("body",
                        Matchers.is("User unable to vote.")));

        Mockito.verify(this.service, Mockito.times(1))
                .vote(Mockito.any(Vote.class));
    }


    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    @DisplayName("Should return 400 if agenda id is invalid on save user vote")
    void shouldReturn400AgendaIdIsInvalidOnSaveUserVote(Long agendaId) throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(agendaId)
                .vote("Não")
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("body",
                        Matchers.is("Invalid value for agenda id.")));

        Mockito.verify(this.service, Mockito.times(0))
                .vote(Mockito.any(Vote.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"nao", "sim", "talvez"})
    @DisplayName("Should return 400 if vote is invalid on save user vote")
    void shouldReturn400VoteIsInvalidOnSaveUserVote(String vote) throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(MocksFactory.faker.number().randomNumber())
                .vote(vote)
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("body",
                        Matchers.is("The vote must be either 'Sim' or 'Não'.")));

        Mockito.verify(this.service, Mockito.times(0))
                .vote(Mockito.any(Vote.class));
    }

    @Test
    @DisplayName("Should return 200 if vote is invalid on save user vote")
    void shouldReturn400VoteIsInvalidOnSaveUserVote() throws Exception {
        String userIdentity = MocksFactory.faker.lorem().word();
        VoteRequest requestParams = VoteRequest
                .builder()
                .agendaId(MocksFactory.faker.number().randomNumber())
                .vote("Sim")
                .build();

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.doNothing().when(this.service).vote(Mockito.any(Vote.class));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(this.URL + "/" + userIdentity + "/vote")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("body",
                        Matchers.is("User vote saved Successfully.")));

        Mockito.verify(this.service, Mockito.times(1))
                .vote(Mockito.any(Vote.class));
    }
}
