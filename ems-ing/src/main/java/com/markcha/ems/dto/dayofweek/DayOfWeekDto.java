package com.markcha.ems.dto.dayofweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.DayOfWeek;
import com.markcha.ems.domain.Week;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayOfWeekDto {
    private Long id;
    private String name;
    public DayOfWeekDto(DayOfWeek dayOfWeek) {
        this.id = dayOfWeek.getId();
        this.name = dayOfWeek.getName();
    }

}
