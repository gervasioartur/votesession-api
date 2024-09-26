package com.votesession.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// To avoid change the value in each side , we use these enum to pu general int values
@Getter
@AllArgsConstructor
public enum GeneralIntEnum {
    // This is the default duration time in minutes when duration is not set
    DEFAULT_DURATION_MIN(1);
    private final int value;
}
