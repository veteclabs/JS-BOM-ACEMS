package com.markcha.ems.repository;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagListDataRepository extends JpaRepository<TagList, Long> {
    List<TagList> findAllByEquipment_Type(EquipmentType type);
}
