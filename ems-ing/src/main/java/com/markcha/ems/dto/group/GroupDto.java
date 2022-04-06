package com.markcha.ems.dto.group;

import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.schedule.ScheduleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private ScheduleDto schedule;

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.schedule = new ScheduleDto(group.getSchedule(), true);
    }
}
