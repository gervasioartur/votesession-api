package com.votesession.service.contracts;

import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.domain.entity.VotingSession;

public interface VotingSessionService {
    VotingSession open(OpenVotingSessionRequest request);
}