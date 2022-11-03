package com.markcha.ems.repository.tag;

import com.markcha.ems.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QTag.tag;

@Repository
public class TagDslRepositoryIml {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;

    public TagDslRepositoryIml(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }

    public Device findAllByGroupId(Long id){

        return query.selectFrom(device)
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         group.id.eq(id)
                        ,equipment.type.eq(EquipmentType.AIR_COMPRESSOR)
                        ,tag.type.in(new ArrayList<>(List.of("COMP_Power", "COMP_Local")))
                ).fetchOne();

    }
    @Transactional
    public Tag getOneById(Long id) {
        return query.selectFrom(tag)
                .where(tag.id.eq(id))
                .fetchOne();
    }
}
