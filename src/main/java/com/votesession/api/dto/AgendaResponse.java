package com.votesession.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AgendaResponse {
    private Long id;
    private String title;
    private String description;
}