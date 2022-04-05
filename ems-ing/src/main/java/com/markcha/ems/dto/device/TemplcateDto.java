package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Voltage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplcateDto {
    private Long id;
    private Long groupId;
    private String groupName;
    private String name;
    private String model;
    private String type;
    private String maker;
    private Double ct;
    private Double pt;
    private Voltage voltage;
    public TemplcateDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        if (!isNull(device.getGroup())) {
            this.groupId = device.getGroup().getId();
            this.groupName = device.getGroup().getName();
        }
        this.model = device.getEquipment().getModel();
        this.type = device.getEquipment().getType();
        this.maker = device.getEquipment().getMaker();
        this.ct = device.getCt();
        this.pt = device.getPt();
        this.voltage = device.getVoltage();
    }
}
