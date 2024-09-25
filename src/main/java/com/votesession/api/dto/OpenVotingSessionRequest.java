package com.votesession.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenVotingSessionRequest {
    //this represents the duration of the voting session in Minutes
    private int duration;
}