package com.markcha.scheduler.dto.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.scheduler.domain.Device;
import com.markcha.scheduler.domain.EquipmentType;
import com.markcha.scheduler.dto.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

@Data
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConDto {
    private Long id;
    private String name;
    private EquipmentType equipmentType;
    private Map<String, TagDto> tags;
    public DeviceConDto(Device device) {
        id = device.getId();
        name = device.getName();
        if (!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .map(t->new TagDto(t, true))
                    .collect(toMap(TagDto::getType, t->t));
        }
        if (!isNull(device.getEquipment())) {
            this.equipmentType = device.getEquipment().getType();
        }
    }
}
