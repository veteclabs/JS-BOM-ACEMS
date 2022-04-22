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

public class WeekGroupDto {
    private Long id;
    private String name;
    private List<CompressorSimpleDto> working = new ArrayList<>();
    private List<CompressorSimpleDto> standBy;
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
        if (!isNull(weekMapper.getOrders())) {
//            List<CompressorSimpleDto> workingGroups = weekMapper.getOrders().stream()
//                    .map(t ->{
//                        return new CompressorSimpleDto(t.getGroup(), t.getOrder());
//                    }).collect(toList());
            this.working = weekMapper.getOrders().stream()
                    .map(t->new CompressorSimpleDto(t.getGroup(), t.getOrder()))
                    .sorted(comparing(CompressorSimpleDto::getOrder))
                    .collect(toList());
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
