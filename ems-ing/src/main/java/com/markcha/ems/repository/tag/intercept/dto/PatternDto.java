package com.markcha.ems.repository.tag.intercept.dto;

import lombok.Data;

@Data
public class PatternDto {
    protected Long id;
    protected Integer beforeSleep;
    protected Integer afterSleep;
    protected char variableName;
    protected Double requireValue;
    protected Integer order;
}
