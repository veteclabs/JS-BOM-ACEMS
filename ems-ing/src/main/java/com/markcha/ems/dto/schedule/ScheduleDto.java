package com.markcha.ems.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Schedule;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.dto.week.WeekGroupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    private Integer min;
    private Integer max;
    @JsonIgnore
    private LocalDateTime updated;
    private List<DayOfWeekDto> dayOfWeeks;
    private List<WeekDto> weeks;
    private List<WeekGroupDto> weekDevices;


    public ScheduleDto(Schedule schedule) {
        this.weekDevices = null;
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
        this.weekDevices = schedule.getWeekMappers().stream()
                .map((wm) -> new WeekGroupDto(wm)).collect(toList());

        this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                .map((wm) -> new DayOfWeekDto(wm.getDayOfWeek()))
                .collect(toList());
    }
}
