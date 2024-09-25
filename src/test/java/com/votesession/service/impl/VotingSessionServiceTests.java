package com.votesession.service.impl;

import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.mocks.MocksFactory;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.VotingSessionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest
public class VotingSessionServiceTests {
    @Autowired
    VotingSessionService service;

    @MockBean
    VotingSessionRepository repository;

    @MockBean
    AgendaRepository agendaRepository;

    @Test
    @DisplayName("Should throw NotFound if agenda does not exist in the system on open voting session")
    void shouldThrowNotFoundIfAgendaDoesNotExistInTheSystemOnOpenVotingSession() {
        OpenVotingSessionRequest request = MocksFactory.openVotingSessionRequestFactory();

        Mockito.when(agendaRepository.findById(request.getAgendaId())).thenReturn(Optional.empty());

        Throwable  exception = Assertions.catchThrowable(() -> this.service.open(request));

        Assertions.assertThat(exception).isInstanceOf(NotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo(
                "Could not find agenda with id " + request.getAgendaId());
        Mockito.verify(agendaRepository, Mockito.times(1)).findById(request.getAgendaId());
    }

    @Test
    @DisplayName("Should open session with default duration time if duration is not set")
    void ShouldOpenSessionWithDefaultDurationTimeIfDurationIsNotSet() {
        OpenVotingSessionRequest request = MocksFactory.openVotingSessionRequestFactory();
        request.setDuration(0);

        Agenda agenda = MocksFactory.agendaWithIdFactory();
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(request, agenda);
        VotingSession savedVotingSession = MocksFactory.votingSessionWithIdFactory(votingSession);

        Mockito.when(agendaRepository.findById(request.getAgendaId())).thenReturn(Optional.of(agenda));
        Mockito.when(repository.save(Mockito.any(VotingSession.class))).thenReturn(savedVotingSession);

        VotingSession result = this.service.open(request);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEndDate())
                .isEqualTo(result.getStartDate().plusMinutes(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue()));
        Mockito.verify(this.agendaRepository, Mockito.times(1)).findById(request.getAgendaId());
        Mockito.verify(this.repository, Mockito.times(1)).save(Mockito.any(VotingSession.class));
    }
}
