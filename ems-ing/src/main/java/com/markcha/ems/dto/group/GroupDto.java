package com.markcha.ems.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {
    private Long id;
    private String name;
    private ScheduleDto schedule;
    @JsonIgnore
    private List<DeviceDto> deviceList;
    private List<GroupDto> airCompressors;
    Map<String, List<DeviceDto>> devices;
    private List<TagDto> tags = new ArrayList<>();
    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        if (!isNull(group.getSchedule())) this.schedule = new ScheduleDto(group.getSchedule(), true);
        this.devices = null;
        this.airCompressors = null;
    }
    public GroupDto(Group group, List<String> typeList, Boolean isParent) {
        this.id = group.getId();
        this.name = group.getName();
        this.schedule = null;
        if (!isNull(group.getDeviceSet())) {
            this.deviceList = group.getDeviceSet().stream()
                    .map(DeviceDto::new)
                    .collect(toList());
        }
        if (!isNull(group.getChildren())) {
            this.airCompressors = group.getChildren().stream()
                    .map(cg-> new GroupDto(cg,typeList, false))
                    .collect(toList());
        }
        if(isParent == false) {
            airCompressors = null;
        }
        devices = this.deviceList.stream()
                .collect(groupingBy(t -> t.getType().getNickname()));
        if(devices.containsKey("airCompressor")) {
            DeviceDto compressor = devices.get("airCompressor").get(0);
            devices.remove("airCompressor");
            this.tags = compressor.getTags();
        } else {
            this.tags = null;
        }
        if(!isNull(this.tags)) {
            if (this.tags.size() == 0) {
                this.tags = null;
            }
        }
    }
}
