package com.markcha.scheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Voltage {
    HIGH("고압"),
    LOW("저압");
    private String description;
}
