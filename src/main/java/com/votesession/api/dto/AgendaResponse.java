package com.votesession.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResponse {
    private Long id;
    private String title;
    private String description;
    private List<VotingSessionResponse> openedVotingSessions;
}