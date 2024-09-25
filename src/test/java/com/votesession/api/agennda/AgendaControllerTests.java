package com.votesession.api.agennda;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votesession.api.agneda.CreateAgendaRequest;
import com.votesession.domain.Agenda;
import com.votesession.mocks.MocksFactory;
import com.votesession.service.contracts.AgendaService;

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
    void shouldReturn500IfUnexpectedExceptionIsThrwon() throws Exception {
        CreateAgendaRequest requestParams = MocksFactory.createAgendaRequestFactory();
        Agenda agenda = MocksFactory.agendaWithNoIdFactory(requestParams);

        String json = new ObjectMapper().writeValueAsString(requestParams);

        Mockito.when(this.mapper.map(requestParams, Agenda.class)).thenReturn(agenda);
        Mockito.doThrow(RuntimeException.class).when(this.service).create(agenda);

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

}
