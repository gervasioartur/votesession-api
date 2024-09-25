package com.votesession.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InverterTextoApiResponse {
    private boolean valid;
    private String formatted;
}
