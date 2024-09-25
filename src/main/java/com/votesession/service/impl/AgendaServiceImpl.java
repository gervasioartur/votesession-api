package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.BusinessException;
import com.votesession.domain.exception.ConflictException;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VoteRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.AgendaService;
import com.votesession.service.contracts.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository repository;
    private final VotingSessionRepository votingSessionRepository;
    private final UserService userService;
    private final VoteRepository voteRepository;

    @Override
    public Agenda create(Agenda agenda) {
        agenda.setActive(true);
        return this.repository.save(agenda);
    }

    @Override
    public List<Agenda> readAll() {
        LocalDateTime now = LocalDateTime.now();
        return this.repository.findAll()
                .stream()
                .map(agenda -> {
                    List<VotingSession> activeVotingSessions = agenda.getVotingSessions() != null ?
                            agenda.getVotingSessions()
                                    .stream()
                                    .filter(votingSession -> votingSession.getEndDate().isAfter(now))
                                    .toList()
                            : List.of();
                    agenda.setVotingSessions(new HashSet<>(activeVotingSessions));
                    return agenda;
                })
                .toList();
    }

    @Override
    public VotingSession openSession(VotingSession votingSession, int duration) {
        Agenda agenda = this.repository
                .findById(votingSession.getAgenda().getId()).
                orElseThrow(() -> new NotFoundException
                        ("Could not find agenda with id " + votingSession.getAgenda().getId()));

        LocalDateTime now = LocalDateTime.now();
        duration = duration == 0 ? GeneralIntEnum.DEFAULT_DURATION_MIN.getValue() : duration;

        votingSession.setActive(true);
        votingSession.setAgenda(agenda);
        votingSession.setStartDate(now);
        votingSession.setEndDate(now.plusMinutes(duration));
        return this.votingSessionRepository.save(votingSession);
    }

    @Override
    public void vote(Vote vote) {
        if(!this.userService.isAbleToVote(vote.getUserId()))
            throw  new BusinessException("User unable to vote.");

        Optional<Agenda> agenda =  this.repository.findById(vote.getAgenda().getId());
        if(agenda.isEmpty()) throw  new NotFoundException("Unable to find agenda with id : "+ vote.getAgenda().getId());

        if(this.voteRepository.findByUserIdAndAgenda_Id(vote.getUserId(), vote.getAgenda().getId()).isPresent())
            throw  new ConflictException("User already voted.");
    }
}
