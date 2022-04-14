package com.markcha.ems.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.Schedule;
import com.markcha.ems.domain.Week;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.dto.week.WeekGroupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {
    private Long id;
    @JsonIgnore
    private Integer interval;
    @JsonIgnore
    private Boolean isGroup;
    private Boolean isActive;
    @JsonIgnore
    private String type;
    private LocalTime startTime;
    private LocalTime stopTime;
    private Double min;
    private Double max;
    @JsonIgnore
    private LocalDateTime updated;
    private List<DayOfWeekDto> dayOfWeeks;
    private List<WeekDto> weeks;
    private List<WeekGroupDto> weekDevices;

    public ScheduleDto(Schedule schedule) {
        this.weeks = null;
        this.id = schedule.getId();
        this.interval = schedule.getInterval();
        this.isGroup = schedule.getIsGroup();
        this.type = schedule.getType();
        this.startTime = schedule.getStartTime();
        this.stopTime = schedule.getStopTime();
        this.updated = schedule.getUpdated();
        this.min = schedule.getMin();
        this.max = schedule.getMax();
        this.isActive = schedule.getIsActive();
    }
    public ScheduleDto(Schedule schedule, Boolean forGroup) {
        this.id = schedule.getId();
        this.interval = schedule.getInterval();
        this.isGroup = schedule.getIsGroup();
        this.type = schedule.getType();
        this.startTime = schedule.getStartTime();
        this.stopTime = schedule.getStopTime();
        this.updated = schedule.getUpdated();
        this.min = schedule.getMin();
        this.max = schedule.getMax();
        this.isActive = schedule.getIsActive();
        if(!isNull(schedule.getWeeks())) {
            this.weekDevices = schedule.getWeeks().stream()
                    .map(t->new WeekGroupDto(t))
                    .sorted(comparing(k->k.getId()))
                    .collect(toList());
        }
        if(!isNull(schedule.getDayOfWeekMappers())) {
            this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                    .map((wm) -> new DayOfWeekDto(wm.getDayOfWeek()))
                    .sorted(comparing(k->k.getId()))
                    .collect(toList());
        }
    }
}
