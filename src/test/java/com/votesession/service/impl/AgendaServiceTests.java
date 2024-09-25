package com.votesession.service.impl;

import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.mocks.MocksFactory;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.AgendaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class AgendaServiceTests {
    @Autowired
    AgendaService service;

    @MockBean
    AgendaRepository repository;

    @MockBean
    VotingSessionRepository votingSessionRepository;

    @Test
    @DisplayName("Should create new agenda")
    void shouldCreateNewAgenda() {
        Agenda agenda = MocksFactory.agendaWithNoIdFactory();
        Agenda savedAgenda = MocksFactory.agendaWithIdFactory(agenda);

        Mockito.when(this.repository.save(agenda)).thenReturn(savedAgenda);

        Agenda result = this.service.create(agenda);

        Mockito.verify(this.repository, Mockito.times(1)).save(agenda);
        Assertions.assertThat(result.getId()).isEqualTo(savedAgenda.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(savedAgenda.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(savedAgenda.getDescription());
        Assertions.assertThat(result.isActive()).isTrue();
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(savedAgenda.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(savedAgenda.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return  list of agendas")
    void shouldReturnListOfAgendas() {
        List<Agenda> agendas = List.of(MocksFactory.agendaWithIdFactory(), MocksFactory.agendaWithIdFactory());

        Mockito.when(this.repository.findAll()).thenReturn(agendas);
        List<Agenda> result = this.service.readAll();

        Mockito.verify(this.repository, Mockito.times(1)).findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(agendas.size());
        Assertions.assertThat(result.getFirst().getId()).isEqualTo(agendas.getFirst().getId());
        Assertions.assertThat(result.getLast().getId()).isEqualTo(agendas.getLast().getId());
    }

    @Test
    @DisplayName("Should throw NotFound if agenda does not exist in the system on open voting session")
    void shouldThrowNotFoundIfAgendaDoesNotExistInTheSystemOnOpenVotingSession() {
        OpenVotingSessionRequest request = MocksFactory.openVotingSessionRequestFactory();

        Mockito.when(repository.findById(request.getAgendaId())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() -> this.service.openSession(request));

        Assertions.assertThat(exception).isInstanceOf(NotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo(
                "Could not find agenda with id " + request.getAgendaId());
        Mockito.verify(repository, Mockito.times(1)).findById(request.getAgendaId());
    }

    @Test
    @DisplayName("Should open session with default duration time if duration is not set")
    void ShouldOpenSessionWithDefaultDurationTimeIfDurationIsNotSet() {
        OpenVotingSessionRequest request = MocksFactory.openVotingSessionRequestFactory();
        request.setDuration(0);

        Agenda agenda = MocksFactory.agendaWithIdFactory();
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(request, agenda);
        VotingSession savedVotingSession = MocksFactory.votingSessionWithIdFactory(votingSession);

        Mockito.when(repository.findById(request.getAgendaId())).thenReturn(Optional.of(agenda));
        Mockito.when(votingSessionRepository.save(Mockito.any(VotingSession.class))).thenReturn(savedVotingSession);

        VotingSession result = this.service.openSession(request);

        int difference = result.getEndDate().compareTo(result.getStartDate());
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(request.getDuration()).isEqualTo(0);
        Assertions.assertThat(difference).isEqualTo(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue());
        Mockito.verify(this.repository, Mockito.times(1)).findById(request.getAgendaId());
        Mockito.verify(this.votingSessionRepository, Mockito.times(1))
                .save(Mockito.any(VotingSession.class));
    }

    @Test
    @DisplayName("Should open session with informed duration time if duration is not set")
    void ShouldOpenSessionInformedDurationTimeIfDurationIsNotSet() {
        OpenVotingSessionRequest request = MocksFactory.openVotingSessionRequestFactory();

        Agenda agenda = MocksFactory.agendaWithIdFactory();
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(request, agenda);
        VotingSession savedVotingSession = MocksFactory.votingSessionWithIdFactory(votingSession);

        Mockito.when(repository.findById(request.getAgendaId())).thenReturn(Optional.of(agenda));
        Mockito.when(votingSessionRepository.save(Mockito.any(VotingSession.class))).thenReturn(savedVotingSession);

        VotingSession result = this.service.openSession(request);

        int difference = result.getEndDate().compareTo(result.getStartDate());
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(request.getDuration()).isGreaterThan(0);
        Assertions.assertThat(difference).isGreaterThan(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue());
        Mockito.verify(this.repository, Mockito.times(1)).findById(request.getAgendaId());
        Mockito.verify(this.votingSessionRepository, Mockito.times(1))
                .save(Mockito.any(VotingSession.class));
    }
}
