package com.votesession.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingResults implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long agendaId;
    private String agendaTitle;
    private String agendaDescription;
    private Result results;
}
