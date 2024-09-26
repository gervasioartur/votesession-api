package com.votesession.service.contracts;

import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.model.VotingResults;

import java.util.List;

public interface AgendaService {
    Agenda create(Agenda agenda);

    List<Agenda> readAll();

    VotingSession openSession(VotingSession votingSession, int duration);

    void vote(Vote vote);

    List<VotingResults> readResults();
}