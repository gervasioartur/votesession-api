package com.votesession.service.contracts;

import com.votesession.domain.entity.VotingSession;

public interface VotingSessionService {
    VotingSession open(VotingSession votingSession);
}