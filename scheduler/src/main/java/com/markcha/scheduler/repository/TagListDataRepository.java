package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.EquipmentType;
import com.markcha.scheduler.domain.TagList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagListDataRepository extends JpaRepository<TagList, Long> {
    List<TagList> findAllByEquipment_Type(EquipmentType type);
}
