package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
public class DeviceDto {
    private Long id;
    private String name;
    private String type;
    private List<TagDto> tags = new ArrayList<>();
    public DeviceDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
        if (!isNull(device.getTagList())) {
            this.tags = device.getTagList();
        }
    }
}
