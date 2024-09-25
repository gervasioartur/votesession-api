package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.repository.AgendaRepository;
import com.votesession.service.contracts.VotingSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class VotingSessionImpl implements VotingSessionService {
    private final AgendaRepository agendaRepository;

    @Override
    public VotingSession open(VotingSession votingSession) {
        this.agendaRepository
                .findById(votingSession.getAgenda().getId()).
                orElseThrow(() -> new NotFoundException
                        ("Could not find agenda with id " + votingSession.getAgenda().getId()));
        return null;
    }
}
