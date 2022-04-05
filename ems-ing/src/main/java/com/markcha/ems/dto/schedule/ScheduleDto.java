package com.markcha.ems.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long id;
    private Integer interval;
    private Boolean isActive;
    private Boolean isGroup;
    private String type;
    private LocalTime startTime;
    private LocalTime stotTime;
    private LocalDateTime updated;
}
