package com.votesession.service.impl;

import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.VotingSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class VotingSessionImpl implements VotingSessionService {
    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository repository;

    @Override
    public VotingSession open(OpenVotingSessionRequest request) {
        Agenda agenda = this.agendaRepository
                .findById(request.getAgendaId()).
                orElseThrow(() -> new NotFoundException
                        ("Could not find agenda with id " + request.getAgendaId()));

        LocalDateTime now = LocalDateTime.now();
        VotingSession votingSession = VotingSession
                .builder()
                .agenda(agenda)
                .startDate(now)
                .endDate(now.plusMinutes(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue()))
                .active(true)
                .build();
        return this.repository.save(votingSession);
    }
}
