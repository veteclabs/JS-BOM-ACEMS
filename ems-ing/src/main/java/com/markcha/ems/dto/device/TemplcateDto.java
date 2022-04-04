package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplcateDto {
    private Long id;
    private String name;
    private String equipmentName;
    private String maker;
    private Double ct;
    private Double pt;
    public TemplcateDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.equipmentName = device.getName();
        this.maker = device.getName();
        this.ct = device.getCt();
        this.pt = device.getPt();
    }
}
