package com.votesession.service.impl;

import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.AgendaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository repository;
    private final VotingSessionRepository votingSessionRepository;

    @Override
    public Agenda create(Agenda agenda) {
        agenda.setActive(true);
        return this.repository.save(agenda);
    }

    @Override
    public List<Agenda> readAll() {
        return this.repository.findAll();
    }

    @Override
    public VotingSession openSession(OpenVotingSessionRequest request) {
        Agenda agenda = this.repository
                .findById(request.getAgendaId()).
                orElseThrow(() -> new NotFoundException
                        ("Could not find agenda with id " + request.getAgendaId()));

        LocalDateTime now = LocalDateTime.now();
        int duration = request.getDuration() == 0 ?
                GeneralIntEnum.DEFAULT_DURATION_MIN.getValue() :
                request.getDuration();

        VotingSession votingSession = VotingSession
                .builder()
                .agenda(agenda)
                .startDate(now)
                .endDate(now.plusMinutes(duration))
                .active(true)
                .build();
        return this.votingSessionRepository.save(votingSession);
    }
}
