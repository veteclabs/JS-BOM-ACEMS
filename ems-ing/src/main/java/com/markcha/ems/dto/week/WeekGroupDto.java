package com.markcha.ems.dto.week;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class WeekGroupDto {
    private Long id;
    private WeekDto week;
    private List<CompressorSimpleDto> working = new ArrayList<>();
    private List<CompressorSimpleDto> standBy;
    @JsonIgnore
    private Week savedWeek;
    public WeekGroupDto(WeekMapper weekMapper) {
        this.id = weekMapper.getId();
        if(!isNull(weekMapper.getWeek())) {
            this.week = new WeekDto(weekMapper.getWeek());
        }
        if (!isNull(weekMapper.getOrders())) {;
            List<CompressorSimpleDto> workingGroups = weekMapper.getOrders().stream()
                    .map(t -> {
                        CompressorSimpleDto workingGroup = new CompressorSimpleDto(t.getGroup(), t.getOrder());
                        CompressorSimpleDto working = new CompressorSimpleDto(t.getGroup(), t.getOrder());
                        this.working.add(working);
                        return workingGroup;
                    }).collect(toList());
            this.working.stream()
                    .sorted(comparing(CompressorSimpleDto::getOrder).reversed());
            if (!isNull(weekMapper.getStandByGroups())) {
                List<CompressorSimpleDto> standByGroups = weekMapper.getStandByGroups().stream()
                        .map(CompressorSimpleDto::new)
                        .collect(toList());
                workingGroups.forEach(t -> t.setOrder(null));
                standByGroups.removeAll(workingGroups);
                this.standBy = standByGroups;
            }
        }
    }
}
