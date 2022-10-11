package com.markcha.ems.repository.tag.intercept.dto;

import com.markcha.ems.domain.pattern.IOType;
import lombok.Data;

@Data
public class PatternListDto {
    protected Long id;
    protected String name;
    protected String description;
    protected Double remoteMinValue;
    protected Double remoteMaxValue;
    protected String formula;
    protected IOType ioType;
}
