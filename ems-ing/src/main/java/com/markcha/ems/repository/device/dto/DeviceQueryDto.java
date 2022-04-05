package com.markcha.ems.repository.device.dto;

import com.markcha.ems.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceQueryDto {
    private Long id;
    private String name;
    private String model;
    private String maker;
    private Double ct;
    private Double pt;

    public DeviceQueryDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.model = device.getName();
        this.maker = device.getName();
        this.ct = device.getCt();
        this.pt = device.getPt();
    }
}
