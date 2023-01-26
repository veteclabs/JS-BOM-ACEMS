package com.markcha.ems.repository.tagList;

import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QTagList.tagList;
import static com.markcha.ems.domain.QTagSet.tagSet;
import static com.markcha.ems.domain.QTagSetMapper.tagSetMapper;

@Repository
public class TagListWithEquipmentRepositoryImpl implements TagListWithEquipmentRepository {
    @Autowired
    private JPAQueryFactory query;
    @Override
    public List<TagList> findAllByEquipmentId(Long equipmentId) {
        return query.selectFrom(tagList)
                .leftJoin(tagList.equipment, equipment).fetchJoin()
                .where(
                        tagList.equipment.id.eq(equipmentId)
                ).fetch();
    }
    @Override
    public List<TagList> findAllByAllEquipmentId(List<String> componentNames) {
        return query.selectFrom(tagList)
                .leftJoin(tagList.equipment, equipment).fetchJoin()
                .leftJoin(tagList.tagSetMappers, tagSetMapper).fetchJoin()
                .leftJoin(tagSetMapper.tagSet, tagSet).fetchJoin()
                .where(
                        tagSetMapper.tagSet.nickname.in(componentNames)
                ).fetch();
    }
}
