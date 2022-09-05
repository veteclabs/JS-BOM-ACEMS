package com.markcha.ems.dto.device;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ComponentsDto {
    List<String> components;
}
