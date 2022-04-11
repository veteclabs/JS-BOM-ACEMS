package com.markcha.ems.repository.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceQueryDto {
    private Long id;
    private Long energyId;
    private String name;
    private List<TagQueryDto> webAccessTags = new ArrayList<>();
    public DeviceQueryDto(Device device) {

        if (!Objects.isNull(device.getEnergy())) {
            this.energyId = device.getEnergy().getId();
        }
        this.name = device.getName();
        if(device.getTags().isEmpty() != true) {
            this.webAccessTags = device.getTags().stream()
                    .map(TagQueryDto::new)
                    .collect(Collectors.toList());
        }
    }


}

