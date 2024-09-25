package com.votesession.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAgendaRequest(
        @NotNull(message = "Title is required.")
        @NotBlank(message = "Title is required.")
        String title,

        String description) {
}