package com.markcha.scheduler.repository.order;

import com.markcha.scheduler.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.scheduler.domain.EquipmentType.AIR_COMPRESSOR;
import static com.markcha.scheduler.domain.QDevice.device;
import static com.markcha.scheduler.domain.QEquipment.equipment;
import static com.markcha.scheduler.domain.QGroup.group;
import static com.markcha.scheduler.domain.QOrder.order1;
import static com.markcha.scheduler.domain.QWeekMapper.weekMapper;

@Repository
@Transactional
public class OrderDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;

    public OrderDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }
    public long deleteByIds(List<Long> ids) {
        return query.delete(order1).where(order1.id.in(ids)).execute();
    }
    public List<Order> findAllByRootGroupIdWeekId(Long rootId, Long weekId) {
        QGroup parentGroup = new QGroup("pg");
        return query.selectFrom(order1)
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(order1.weekMapper, weekMapper).fetchJoin()
                .where(
                         parentGroup.id.eq(rootId)
                        ,weekMapper.week.id.eq(weekId)
                ).fetch();
    }

    public List<Order> findAllByIds(List<Long> ids) {
        return query.selectFrom(order1)
                .leftJoin(order1.group, group).fetchJoin()
                .where(group.id.in(ids))
                .fetch();
    }
    public List<Order> findAllByDeviceId(Long id) {
        return query.selectFrom(order1)
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         device.id.eq(id)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                ).fetch();
    }
    public List<Order> findAllByDeviceIds(List<Long> ids) {
        return query.selectFrom(order1)
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         device.id.in(ids)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                ).fetch();
    }
}
