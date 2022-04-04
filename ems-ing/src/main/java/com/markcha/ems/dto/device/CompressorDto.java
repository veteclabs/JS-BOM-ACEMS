package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorDto {
    private Long id;
    private String name;
    private String equipmentName;
    private String maker;
    public CompressorDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.equipmentName = device.getName();
        this.maker = device.getName();
    }
}
