package com.markcha.ems.dto.week;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class WeekGroupDto {
    private Long id;
    private String name;
    private List<CompressorSimpleDto> working;
    private List<CompressorSimpleDto> standBys;
    @JsonIgnore
    private Week savedWeek;
    public WeekGroupDto(Week week) {
        this.id = week.getId();
        this.name = week.getName();
        this.savedWeek = week;
    }
}
