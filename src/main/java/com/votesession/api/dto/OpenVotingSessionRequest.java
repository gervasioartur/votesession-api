package com.votesession.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OpenVotingSessionRequest {
    @NotNull(message = "Agenda id is required.")
    @NotBlank(message = "Agenda id is required.")
    private Long agendaId;

    //this represents the duration of the voting session in Minutes
    private int duration;
}