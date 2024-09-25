package com.votesession.service.impl;

import com.votesession.domain.entity.VotingSession;
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
        VotingSession session = MocksFactory.votingSessionWithNoIdFactory();

        Mockito.when(agendaRepository.findById(session.getAgenda().getId())).thenReturn(Optional.empty());

        Throwable  exception = Assertions.catchThrowable(() -> this.service.open(session));

        Assertions.assertThat(exception).isInstanceOf(NotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo(
                "Could not find agenda with id " + session.getAgenda().getId());
        Mockito.verify(agendaRepository, Mockito.times(1)).findById(session.getAgenda().getId());
    }
}
