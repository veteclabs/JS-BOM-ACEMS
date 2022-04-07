package com.markcha.ems.dto.week;


import com.markcha.ems.domain.DayOfWeek;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.WeekMapper;
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
    public WeekGroupDto(WeekMapper weekMapper) {
        this.id = weekMapper.getWeek().getId();
        this.name = weekMapper.getWeek().getName();
        this.working = weekMapper.getGroup().getDeviceSet().stream()
                .map(CompressorSimpleDto::new)
                .collect(toList());
        this.standBys = weekMapper.getGroup().getStandByDeivces().stream()
                .map(CompressorSimpleDto::new)
                .collect(toList());
        if(!isNull(standBys)) {
            this.standBys.stream().forEach((standBy) -> {
                this.working.stream().forEach((working) -> {
                    if (!isNull(standBy) && !isNull(working)) {
                        if (standBy.getId() == working.getId()) {
                            standBys.remove(standBy);
                        }
                    }
                });
            });
        }

    }
}
