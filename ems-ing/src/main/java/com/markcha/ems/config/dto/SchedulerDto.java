package com.markcha.ems.config.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class SchedulerDto {

    private Long scheduleId;
    private Long groupId;
    private Integer interval;
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
    private List<Integer> dayOfWeeks;
    private List<Integer> weeks;
    //        private List<TagDto> tags = new ArrayList<>();
    private Object pressure;
    public SchedulerDto(Group group) {
        if(!isNull(group.getDeviceSet())) {
            this.groupId = group.getId();
        }
        if(!isNull(group.getSchedule())) {
            Schedule schedule = group.getSchedule();
            this.scheduleId = schedule.getId();
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
                this.weeks = schedule.getWeekMappers().stream()
                        .map(t->t.getWeek().getIdx())
                        .sorted(comparing(k->k))
                        .collect(toList());
            }
            if(!isNull(schedule.getDayOfWeekMappers())) {
                this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                        .map(t->t.getDayOfWeek().getIdx())
                        .collect(toList());
                this.dayOfWeeks = new ArrayList<>(new HashSet<>(this.dayOfWeeks));
            }

        }
        if(!isNull(group.getTagetDevice())) {
            if(!isNull(group.getTagetDevice().getPressure())) {
                this.pressure = group.getTagetDevice().getPressure();
            }
        }
    }
}