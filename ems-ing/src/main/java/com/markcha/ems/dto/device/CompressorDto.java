package com.markcha.ems.dto.device;

import com.markcha.ems.domain.DayOfWeek;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Schedule;
import com.markcha.ems.domain.Week;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.week.WeekDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorDto {
    private Long id;
    private String name;
    private String group;
    private String model;
    private String maker;
    private Boolean scheduleIsActive;
    private List<String> dayOfWeeks;
    private List<String> weeks;
    public CompressorDto(Device device) {
        this.id = device.getId();
        this.model = device.getEquipment().getName();
        this.maker = device.getEquipment().getMaker();
        if(!Objects.isNull(device.getGroup())
                && !Objects.isNull(device.getGroup().getSchedule())) {
            Schedule schedule = device.getGroup().getSchedule();
            this.name = device.getGroup().getName();
            this.scheduleIsActive = schedule.getIsActive();
            this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                    .map((dowmp) -> dowmp.getDayOfWeek().getName())
                    .collect(toList());
            this.weeks = schedule.getWeekMappers().stream()
                    .map((wmp) -> wmp.getWeek().getName())
                    .collect(toList());
        }
    }
}
