package com.markcha.ems.dto.dayofweek;

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
    private Integer idx;
    private String name;
    public DayOfWeekDto(DayOfWeek dayOfWeek) {
        this.id = dayOfWeek.getId();
        this.idx = dayOfWeek.getIdx();
        this.name = dayOfWeek.getName();
    }
}
