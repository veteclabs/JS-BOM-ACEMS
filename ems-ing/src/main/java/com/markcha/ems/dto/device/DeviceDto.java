package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class DeviceDto {
    private Long id;
    private String name;
    @JsonIgnore
    private Long deviceId;
    @JsonIgnore
    private EquipmentType type;
    private List<TagDto> tags = new ArrayList<>();
    @JsonIgnore
    private Long groupId;
    public DeviceDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.deviceId = device.getId();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
        if (!isNull(device.getTagList())) {
            this.tags = device.getTagList();
        }
        if (!isNull(device.getGroup())) {
            this.groupId = device.getGroup().getId();
        }
    }
    public DeviceDto(Device device, Boolean forGroup) {
        if(!isNull(device.getGroup())) {
            this.id = device.getGroup().getId();
            this.name = device.getGroup().getName();
        }
        this.deviceId = device.getId();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
        if (!isNull(device.getTagList())) {
            this.tags = device.getTagList();
        }

    }

}
