package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorSimpleDto {
    private Long id;
    private String name;

    public CompressorSimpleDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
    }
    public CompressorSimpleDto(Device device) {
        this.id = device.getId();
        this.name = device.getName();
    }
}
