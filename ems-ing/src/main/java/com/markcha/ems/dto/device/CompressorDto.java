package com.markcha.ems.dto.device;

import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorDto {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
    private String model;
    private String maker;
    private List<String> dayOfWeeks;
    private List<String> weeks;
    private ScheduleDto schedule;
    public CompressorDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.model = device.getEquipment().getName();
        this.maker = device.getEquipment().getMaker();
        if(!isNull(device.getGroup())) {
            Group group = device.getGroup();
            if (!isNull(group.getParent())) {
                Group parentGroup = group.getParent();
                this.groupId = parentGroup.getId();
                this.groupName = parentGroup.getName();
            }
            if (!isNull(group.getSchedule())) {
                Schedule schedule = group.getSchedule();
                ScheduleDto scheduleDto = new ScheduleDto(schedule);
                scheduleDto.setDayOfWeeks(schedule.getDayOfWeekMappers().stream()
                        .map((dowmp) -> {
                            return new DayOfWeekDto(dowmp.getDayOfWeek());
                        })
                        .sorted(Comparator.comparing(DayOfWeekDto::getId))
                        .collect(toList()));
                scheduleDto.setWeeks(schedule.getWeekMappers().stream()
                        .map((wmp) -> {
                            return new WeekDto(wmp.getWeek());
                        })
                        .sorted(Comparator.comparing(WeekDto::getId))
                        .collect(toList()));
                this.weeks = scheduleDto.getWeeks().stream()
                        .map(WeekDto::getName)
                        .collect(toList());
                this.dayOfWeeks = scheduleDto.getDayOfWeeks().stream()
                        .map(DayOfWeekDto::getName)
                        .collect(toList());
                this.schedule = scheduleDto;
            }
        }
    }
}
