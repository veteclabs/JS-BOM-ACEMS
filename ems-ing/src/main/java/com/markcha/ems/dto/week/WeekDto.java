package com.markcha.ems.dto.week;

import com.markcha.ems.domain.Week;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekDto {
    private Long id;
    private String name;

    public WeekDto(Week week) {
        this.id = week.getId();
        this.name = week.getName();
    }
}
