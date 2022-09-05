package com.markcha.ems.repository.tagList;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;

import java.util.List;

public interface TagListWithEquipmentRepository {
    List<TagList> findAllByEquipomentType(EquipmentType type);
}
