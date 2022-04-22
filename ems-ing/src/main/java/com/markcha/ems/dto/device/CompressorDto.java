package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.week.WeekDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorDto {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
    private ScheduleDto schedule;
    private EquipmentDto equipment;
    @JsonIgnore
    private List<TagDto> tags;
    public CompressorDto(Device device) {
        if (!isNull(device.getEquipment())) {
            equipment = new EquipmentDto(device.getEquipment());
        }
        if(!isNull(device.getGroup())) {
            Group group = device.getGroup();
            this.id = device.getId();
            this.name = device.getName();
            if (!isNull(group.getParent())) {
                Group parentGroup = group.getParent();
                this.groupId = parentGroup.getId();
                this.groupName = parentGroup.getName();
            }
            if (!isNull(group.getSchedule())) {
                Schedule schedule = group.getSchedule();
                this.schedule = new ScheduleDto(schedule);
            }
        }
        if (!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .map(TagDto::new)
                    .collect(toList());
        }
    }
}
