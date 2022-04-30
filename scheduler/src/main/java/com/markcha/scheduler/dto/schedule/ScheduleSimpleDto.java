package com.markcha.scheduler.dto.schedule;

import com.markcha.scheduler.domain.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ScheduleSimpleDto {
    private Long id;
    private Integer interval;
    private Boolean isGroup;
    private Boolean isActive;
    private LocalDateTime updated;
    public ScheduleSimpleDto(Schedule schedule) {
        this.id = schedule.getId();
        this.interval = schedule.getInterval();
        this.isGroup = schedule.getIsGroup();
        this.updated = schedule.getUpdated();
        this.isActive = schedule.getIsActive();
    }
}