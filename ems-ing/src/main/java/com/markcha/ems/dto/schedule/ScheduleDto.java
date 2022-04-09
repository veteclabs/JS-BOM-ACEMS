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
    private Integer min;
    private Integer max;
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
            weekDevices = schedule.getWeeks().stream()
                    .map(t->t.getWeek())
                    .distinct()
                    .sorted(comparing(Week::getId))
                    .map(WeekGroupDto::new)
                    .collect(toList());
            List<CompressorSimpleDto> deviceList = schedule.getStandByGroups().stream()
                    .map(t -> new CompressorSimpleDto(t, null))
                    .distinct()
                    .collect(toList());
            deviceList.forEach(t-> System.out.println(t.getName()));
            Map<Long, List<CompressorSimpleDto>> grouppingWeek = schedule.getWeeks().stream()
                    .collect(
                            groupingBy(t -> t.getWeek().getId(),
                            mapping(t -> new CompressorSimpleDto(t.getGroup(), t.getOrder()), toList())));

            weekDevices.forEach(t-> {
                List<CompressorSimpleDto> compressorSimpleDtos = grouppingWeek.get(t.getId());
                compressorSimpleDtos.removeIf(rm->rm.getId() == null);
                List<CompressorSimpleDto> workingDevices = compressorSimpleDtos.stream()
                        .sorted(comparing(CompressorSimpleDto::getOrder))
                        .collect(toList());
                workingDevices.forEach(n->n.setOrder(null));
                List<CompressorSimpleDto> standByDeivces = new ArrayList<>();
                deviceList.forEach(ad->standByDeivces.add(ad));
                standByDeivces.removeAll(workingDevices);
                t.setWorking(workingDevices);
                t.setStandBys(standByDeivces);
            });

        }
        if(!isNull(schedule.getDayOfWeekMappers())) {
            this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                    .map((wm) -> new DayOfWeekDto(wm.getDayOfWeek()))
                    .collect(toList());
        }
    }
}
