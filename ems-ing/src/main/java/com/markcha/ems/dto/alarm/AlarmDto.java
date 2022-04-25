package com.markcha.ems.dto.alarm;

import com.markcha.ems.domain.Alarm;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

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