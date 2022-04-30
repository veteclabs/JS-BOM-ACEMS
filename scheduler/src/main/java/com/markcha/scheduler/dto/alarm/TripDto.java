package com.markcha.scheduler.dto.alarm;

import com.markcha.scheduler.domain.Trip;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripDto {
    private Long id;
    private Integer code;
    private String message;
    private Boolean isTrip;
    private Boolean isWarning;
    public TripDto(Trip trip) {
        this.id = trip.getId();
        this.code = trip.getCode();
        this.message = trip.getMessage();
        this.isTrip = trip.getIsTrip();
        this.isWarning = trip.getIsWarning();
    }
}
