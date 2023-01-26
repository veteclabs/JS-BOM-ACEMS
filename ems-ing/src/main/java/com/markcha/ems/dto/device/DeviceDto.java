package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDto {
    private Long id;
    private String name;
    @JsonIgnore
    private Long deviceId;
    @JsonIgnore
    private EquipmentType type;
    private Map<String, TagDto> tags = new HashMap<>();
    @JsonIgnore
    private Long groupId;
    Map<String, List<DeviceDto>> devices;
    public DeviceDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.deviceId = device.getId();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
        if (!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .sorted(Comparator.comparing(k->k.getTagList().getId()))
                    .map(t->new TagDto(t,true))
                    .collect(toMap(TagDto::getType, t -> t, (p1,p2) -> p1));
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
            this.tags = device.getTagList().stream()
                    .collect(toMap(TagDto::getType, t -> t));
        }
    }
    public DeviceDto(Device device, Boolean forGroup, Boolean deviceShow) {
        if(!isNull(device.getGroup())) {
            this.id = device.getGroup().getId();
            this.name = device.getGroup().getName();
            if (!isNull(device.getGroup().getDeviceSet())) {
                this.devices = device.getGroup().getDeviceSet().stream()
                        .map(t->new DeviceDto(t))
                        .collect(groupingBy(t->t.getType().getNickname()));
            }
        } else {
            this.id = device.getId();
            this.name = device.getName();
        }
        this.deviceId = device.getId();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
        if (!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .map(TagDto::new)
                    .collect(toMap(TagDto::getType, t -> t));
        }

    }

}
