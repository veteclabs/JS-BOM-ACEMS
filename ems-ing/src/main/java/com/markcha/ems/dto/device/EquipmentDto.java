package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.EquipmentType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquipmentDto {
    private Long equipmentId;
    private String model;
    private String maker;
    private EquipmentType type;
    private String description;
    public EquipmentDto(Equipment equipment) {
        this.equipmentId = equipment.getId();
        this.model = equipment.getModel();
        this.description = equipment.getDescription();
        this.maker = equipment.getMaker();
        this.type = equipment.getType();
    }
}
