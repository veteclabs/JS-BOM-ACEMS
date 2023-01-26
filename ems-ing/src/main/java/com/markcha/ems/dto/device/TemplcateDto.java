package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Voltage;
import com.markcha.ems.repository.group.dto.TagQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private List<TagQueryDto> tags;
    public TemplcateDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        if (!isNull(device.getGroup())) {
            this.groupId = device.getGroup().getId();
            this.groupName = device.getGroup().getName();
        }
        this.model = device.getEquipment().getModel();
        this.type = device.getEquipment().getDescription();
        this.maker = device.getEquipment().getMaker();
        this.ct = device.getCt();
        this.pt = device.getPt();
        this.voltage = device.getVoltage();
    }
    public TemplcateDto(Device device, Boolean showTag) {
        this.id = device.getId();
        this.name = device.getName();
        if (!isNull(device.getGroup())) {
            this.groupId = device.getGroup().getId();
            this.groupName = device.getGroup().getName();
        }
        this.model = device.getEquipment().getModel();
        this.type = device.getEquipment().getDescription();
        this.maker = device.getEquipment().getMaker();
        this.ct = device.getCt();
        this.pt = device.getPt();
        this.voltage = device.getVoltage();
        if(!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .map(TagQueryDto::new)
                    .collect(toList());
        }
    }
}
