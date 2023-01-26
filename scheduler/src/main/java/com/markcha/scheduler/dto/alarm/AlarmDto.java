package com.markcha.scheduler.dto.alarm;

import com.markcha.scheduler.domain.Alarm;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class AlarmDto {
    private Long id;
    private String message;
    private LocalDate eventDate;
    private LocalTime occurrenceTime;
    private Boolean checkIn;
    private LocalTime recoverTime;
    private LocalDate recoverDate;
    private Double tempValue;
    private Double prssValue;
    private Double kwhValue;
    private String type;
    public AlarmDto(Alarm alarm) {
        id = alarm.getId();
        message = alarm.getMessage();
        eventDate = alarm.getEventDate();
        occurrenceTime = alarm.getOccurrenceTime();
        checkIn = alarm.getCheckIn();
        recoverDate = alarm.getRecoverDate();
        recoverTime = alarm.getRecoverTime();
        tempValue = alarm.getTempValue();
        prssValue = alarm.getPrssValue();
        kwhValue = alarm.getKwhValue();
        type = alarm.getType();
    }
}