package com.markcha.ems.dto.group;

import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.schedule.ScheduleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private ScheduleDto schedule;

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        if (!isNull(group.getSchedule())) this.schedule = new ScheduleDto(group.getSchedule(), true);
    }
}
