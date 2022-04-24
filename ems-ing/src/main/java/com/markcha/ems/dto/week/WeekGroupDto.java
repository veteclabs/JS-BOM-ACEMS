package com.markcha.ems.dto.week;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekGroupDto {
    private Long id;
    private String name;
    private List<CompressorSimpleDto> working = new ArrayList<>();
    private List<CompressorSimpleDto> standBy = new ArrayList<>();
    public WeekGroupDto(Long id, String name, List<CompressorSimpleDto> working) {
        this.id = id;
        this.name = name;
        this.working = working;
    }
    public WeekGroupDto(WeekMapper weekMapper) {
        if(!isNull(weekMapper.getWeek())) {
            this.id = weekMapper.getWeek().getId();
            this.name = weekMapper.getWeek().getName();
        }
        if(!isNull(weekMapper.getStandByGroups())){
            this.standBy = weekMapper.getStandByGroups().stream()
                    .map(t->new CompressorSimpleDto(t))
                    .collect(toList());
        }
        if (!isNull(weekMapper.getOrders())) {
            this.working = weekMapper.getOrders().stream()
                    .map(t ->{
                        return new CompressorSimpleDto(t.getGroup(), t.getOrder());
                    })
                    .sorted(comparing(CompressorSimpleDto::getOrder)).collect(toList());
        }
    }
}
