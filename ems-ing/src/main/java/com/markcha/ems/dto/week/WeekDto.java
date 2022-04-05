package com.markcha.ems.dto.week;

import com.markcha.ems.domain.Week;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekDto {
    private Long id;
    private Integer idx;
    private String name;

    public WeekDto(Week week) {
        this.id = week.getId();
        this.idx = week.getIdx();
        this.name = week.getName();
    }
}
