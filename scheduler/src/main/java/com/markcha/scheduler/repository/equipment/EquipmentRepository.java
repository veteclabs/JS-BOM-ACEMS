package com.markcha.scheduler.repository.equipment;

import com.markcha.scheduler.domain.Equipment;
import com.markcha.scheduler.domain.EquipmentType;

public interface EquipmentRepository {
    public Equipment getOneByTypeAndModel(String type, String model);
    public Equipment getOneById(Long id);
    public Equipment getOneByType(EquipmentType type);
}
