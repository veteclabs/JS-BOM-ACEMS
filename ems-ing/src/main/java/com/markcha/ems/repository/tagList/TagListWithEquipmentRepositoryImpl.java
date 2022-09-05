package com.markcha.ems.repository.tagList;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QTagList.tagList;

@Repository
public class TagListWithEquipmentRepositoryImpl implements TagListWithEquipmentRepository {
    @Autowired
    private JPAQueryFactory query;
    @Override
    public List<TagList> findAllByEquipomentType(EquipmentType type) {
        return query.selectFrom(tagList)
                .leftJoin(tagList.equipment, equipment).fetchJoin()
                .where(
                        tagList.equipment.type.eq(type)
                ).fetch();
    }
}
