package com.markcha.ems.repository;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import com.markcha.ems.repository.tagList.TagListWithEquipmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagListDataRepository extends JpaRepository<TagList, Long> , TagListWithEquipmentRepository {
    List<TagList> findAllByEquipment_Type(EquipmentType type);
}
