package com.markcha.ems.dto.week;


import com.markcha.ems.domain.DayOfWeek;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.WeekMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WeekGroupDto {
    private Long id;
    private String name;
    private List<Device> working;
    private List<Device> standBy;
    public WeekGroupDto(WeekMapper weekMapper) {
        this.id = weekMapper.getWeek().getId();
        this.name = weekMapper.getWeek().getName();
    }
}
