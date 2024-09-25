package com.votesession.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralIntEnum {
    DEFAULT_DURATION_MIN(1);
    private final int value;
}
