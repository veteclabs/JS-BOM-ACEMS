package com.markcha.ems.repository;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import com.markcha.ems.repository.tagList.TagListWithEquipmentRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagListDataRepository extends JpaRepository<TagList, Long> , TagListWithEquipmentRepository {
    List<TagList> findAllByEquipment_Type(EquipmentType type);
    @Query(value="" +
            "select tl " +
            "from TagList tl " +
            "left join fetch tl.patternList pl " +
            "left join fetch tl.equipment eq " +
            "where tl.type = :type" +
            "")
    List<TagList> findAllDontHavePatternList(@Param("type") String type);
}
