package com.markcha.ems.dto.device;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompressorModelDto {
    private Long equipmentId;
    private String model;
}
