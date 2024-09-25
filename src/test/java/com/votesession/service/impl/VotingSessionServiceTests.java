package com.votesession.service.impl;

import com.votesession.api.dto.OpenVotingSessionRequest;
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
}
