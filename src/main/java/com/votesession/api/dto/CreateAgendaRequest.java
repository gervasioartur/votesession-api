package com.votesession.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateAgendaRequest {
    @NotNull(message = "Title is required.")
    @NotBlank(message = "Title is required.")
    private String title;

    private String description;
}