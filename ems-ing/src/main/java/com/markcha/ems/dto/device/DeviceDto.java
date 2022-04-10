package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import lombok.Data;

import static java.util.Objects.isNull;

@Data
public class DeviceDto {
    private Long id;
    private String name;
    private String type;

    public DeviceDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        if (!isNull(device.getEquipment())) this.type = device.getEquipment().getType();
    }
}
