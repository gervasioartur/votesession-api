package com.votesession.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingSessionResponse {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}