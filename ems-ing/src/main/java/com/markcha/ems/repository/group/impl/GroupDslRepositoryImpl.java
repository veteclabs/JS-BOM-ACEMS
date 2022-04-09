package com.markcha.ems.repository.group.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.group.GroupRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QOrder.order1;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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

    private List<WeekMapper> getWeekMapper(List<Long> scheduleIds) {
        return queryFactory.select(weekMapper)
                .from(weekMapper).distinct()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device)
                .leftJoin(device.equipment, equipment)
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         weekMapper.schedule.id.in(scheduleIds)
                        ,equipment.type.eq("compressor").or(
                                order1.isNull()
                        )
                )
                .fetch();
    }
    private List<Group> getRootGroups() {
        return queryFactory.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(
                        group.type.eq("group")
                )
                .fetch();
    }

    private List<Group> getChildGroups(List<Long> parentGroupIds) {
        QGroup parentGroup = new QGroup("cg");
        return queryFactory.selectFrom(group)
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         parentGroup.id.in(parentGroupIds)
                        ,equipment.type.eq("compressor")
                )
                .fetch();
    }


    @Override
    public List<Group> findAllJoinSchedule() {
        List<Group> rootGroup = getRootGroups();
        List<Long> scheduleIds = rootGroup.stream()
                .map((t) -> t.getSchedule().getId())
                .collect(toList());
        List<Long> parentGroupIds = rootGroup.stream()
                .map((t) -> t.getId())
                .collect(toList());
        List<WeekMapper> weekMappers = getWeekMapper(scheduleIds);

        List<Group> childGroups = getChildGroups(parentGroupIds);
        Map<Long, List<Group>> groupByParentId = childGroups.stream()
                .collect(groupingBy(t -> t.getParent().getId()));
        weekMappers.forEach(t->
            t.getSchedule().getGroups().forEach(k->{
                List<Group> groups = groupByParentId.get(k.getId());
                t.setStandByGroups(groups);
            }));

        Map<Long, List<WeekMapper>> grouppingWeekMapper = weekMappers.stream()
                .collect(groupingBy(weekMapper -> weekMapper.getSchedule().getId()));
        rootGroup.forEach(t->t.getSchedule().setWeeks(grouppingWeekMapper.get(t.getSchedule().getId())));

        return rootGroup;

    }
}
