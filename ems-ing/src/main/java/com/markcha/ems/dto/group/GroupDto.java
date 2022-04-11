package com.markcha.ems.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {
    private Long id;
    private String name;
    private ScheduleDto schedule;
    private List<DeviceDto> devices;
    private List<GroupDto> airCompressors;
    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        if (!isNull(group.getSchedule())) this.schedule = new ScheduleDto(group.getSchedule(), true);
        this.devices = null;
        this.airCompressors = null;
    }
    public GroupDto(Group group, Boolean isParent) {
        this.id = group.getId();
        this.name = group.getName();
        this.schedule = null;
        if (!isNull(group.getDeviceSet())) {
            this.devices = group.getDeviceSet().stream()
                    .map(DeviceDto::new)
                    .collect(toList());
        }
        if (!isNull(group.getChildren())) {
            this.airCompressors = group.getChildren().stream()
                    .map(cg-> new GroupDto(cg, false))
                    .collect(toList());
        }
        if(isParent == false) {
            airCompressors = null;
        }
    }
}
