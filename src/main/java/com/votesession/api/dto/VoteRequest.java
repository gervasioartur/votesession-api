package com.votesession.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    //this represents the duration of the voting session in Minutes
    @Min(value = 1, message = "Invalid value for agenda id.")
    private Long agendaId;

    @Pattern(regexp = "Sim|Não", message = "The vote must be either 'Sim' or 'Não'.")
    private String vote;
}