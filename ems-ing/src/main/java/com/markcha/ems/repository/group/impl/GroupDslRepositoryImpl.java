package com.markcha.ems.repository.group.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.group.GroupRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;

@Repository
public class GroupDslRepositoryImpl implements GroupRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public GroupDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    @Override
    public List<Group> findAllByType(String type) {
        return queryFactory.select(group)
                .from(group)
                .where(
                        group.type.eq(type)
                ).fetch();
    }
    @Override
    public Group getOneById(Long id) {
        return queryFactory.select(group)
                .from(group)
                .where(group.id.eq(id))
                .fetchOne();
    }

    @Override
    public Group getOneByDeviceId(Long id) {
        return queryFactory.select(group)
                .from(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .where(device.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<Group> findAllJoinSchedule() {
        QGroup weekGroup = new QGroup("wg");
        return queryFactory.select(group)
                .from(group)
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(weekMapper.group, weekGroup).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(group.type.eq("group"),
                        weekGroup.parent.id.eq(group.id))
                .fetch();

    }
}
