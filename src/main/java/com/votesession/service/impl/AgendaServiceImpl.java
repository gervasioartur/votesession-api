package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.BusinessException;
import com.votesession.domain.exception.ConflictException;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.domain.model.Result;
import com.votesession.domain.model.VotingResults;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VoteRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.AgendaService;
import com.votesession.service.contracts.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/*
 * This class deal with Agenda business rules
 * */

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository repository;
    private final VotingSessionRepository votingSessionRepository;
    private final UserService userService;
    private final VoteRepository voteRepository;

    // Creates new agenda based on the param
    @Override
    public Agenda create(Agenda agenda) {
        agenda.setActive(true);
        return this.repository.save(agenda);
    }

    //Reads all agendas with opened voting sessions
    @Override
    public List<Agenda> readAll() {
        LocalDateTime now = LocalDateTime.now();
        return this.repository.findAll()
                .stream()
                .map(agenda -> {
                    // It filters only int ime voting sessions
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

    /* Opens the agenda voting session based on the param,
     * If the agenda can not be found by the id, throws NotFoundException
     */
    @Override
    public VotingSession openSession(VotingSession votingSession, int duration) {
        Agenda agenda = this.repository
                .findById(votingSession.getAgenda().getId()).
                orElseThrow(() -> new NotFoundException
                        ("Could not find agenda with id " + votingSession.getAgenda().getId()));

        // Here it takes the default time duration if the duration is not set
        LocalDateTime now = LocalDateTime.now();
        duration = duration == 0 ? GeneralIntEnum.DEFAULT_DURATION_MIN.getValue() : duration;

        votingSession.setActive(true);
        votingSession.setAgenda(agenda);
        votingSession.setStartDate(now);
        votingSession.setEndDate(now.plusMinutes(duration));
        return this.votingSessionRepository.save(votingSession);
    }

    /*
     * Has responsibility of saving user vote,
     * If the user document(CPF) is invalid, throws BusinessException
     * If the agenda can not be found by the id, throws NotFoundException
     * If the user has already voted, throws ConflictException
     * If there's no active voting session, throws BusinessException
     */
    @Override
    @CacheEvict(value = "voting_results", allEntries = true)
    public void vote(Vote vote) {
        if (!this.userService.isAbleToVote(vote.getUserId()))
            throw new BusinessException("User unable to vote.");

        Optional<Agenda> agenda = this.repository.findById(vote.getAgenda().getId());
        if (agenda.isEmpty())
            throw new NotFoundException("Unable to find agenda with id : " + vote.getAgenda().getId());

        if (this.voteRepository.findByUserIdAndAgenda_Id(vote.getUserId(), vote.getAgenda().getId()).isPresent())
            throw new ConflictException("User already voted.");

        // Getting opened voting sessions
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveVotingSession = agenda.get().getVotingSessions() != null &&
                agenda.get().getVotingSessions()
                        .stream()
                        .anyMatch(votingSession -> votingSession.getEndDate().isAfter(now));

        if (!hasActiveVotingSession) throw new BusinessException("Could not find active voting session.");

        vote.setActive(true);
        vote.setAgenda(agenda.get());
        this.voteRepository.save(vote);
    }

    /*
     * Has responsibility of calculating the voting results,
     */
    @Override
    @Cacheable(value = "voting_results")
    public List<VotingResults> readResults() {
        List<Agenda> agendas = this.repository.findAll();
        return agendas.stream()
                .map(agenda -> {
                    // calculating in favor votes
                    int inFavor = (int) agenda.getVotes().stream()
                            .filter(vote -> "Sim".equalsIgnoreCase(vote.getVote()) && vote.isActive())
                            .count();

                    // calculating against votes
                    int against = (int) agenda.getVotes().stream()
                            .filter(vote -> "Não".equalsIgnoreCase(vote.getVote()) && vote.isActive())
                            .count();

                    //Building the results
                    Result results = Result.builder()
                            .inFavor(inFavor)
                            .against(against)
                            .build();

                    // Returning the results with agenda info
                    return VotingResults.builder()
                            .agendaId(agenda.getId())
                            .agendaTitle(agenda.getTitle())
                            .agendaDescription(agenda.getDescription())
                            .results(results)
                            .build();
                })
                .toList();
    }
}