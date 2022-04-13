package com.markcha.ems.repository.equipment;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.EquipmentType;

public interface EquipmentRepository {
    public Equipment getOneByTypeAndModel(String type, String model);
    public Equipment getOneById(Long id);
    public Equipment getOneByType(EquipmentType type);
}
