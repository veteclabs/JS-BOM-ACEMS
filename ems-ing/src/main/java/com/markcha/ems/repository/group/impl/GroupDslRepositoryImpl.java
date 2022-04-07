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
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
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
    private List<Group> getGroupByMapperIdAndParentGroupId(List<Long> mapperIds, List<Long> parentGroupIds) {
        return queryFactory.select(group)
                .from(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .where(
                         group.id.in(parentGroupIds)
                        ,group.weekMapper.id.in(mapperIds)
                        )
                .fetch();

    }
    private List<WeekMapper> getWeekMapper(List<Long> scheduleIds, List<Long> parentGroupIds) {
        return queryFactory.select(weekMapper)
                .from(weekMapper)
                .leftJoin(weekMapper.group, group).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                        weekMapper.schedule.id.in(scheduleIds)
                        ,group.parent.id.in(parentGroupIds)
                )
                .fetch();
    }
    private List<Group> getRootGroup() {
        return queryFactory.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(group.type.eq("group")
                )
                .fetch();
    }

    private List<Device> getChildGroupDevices(List<Long> parentGroupIds) {
        QGroup parentGroup = new QGroup("cg");
        return queryFactory.selectFrom(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .where(parentGroup.id.in(parentGroupIds))
                .fetch();
    }


    @Override
    public List<Group> findAllJoinSchedule() {
        List<Group> rootGroup = getRootGroup();
        List<Long> scheduleIds = rootGroup.stream()
                .map((t) -> t.getSchedule().getId())
                .collect(toList());
        List<Long> parentGroupIds = rootGroup.stream()
                .map((t) -> t.getId())
                .collect(toList());
        List<WeekMapper> weekMappers = getWeekMapper(scheduleIds, parentGroupIds);

        List<Device> childGroupDevices = getChildGroupDevices(parentGroupIds);
        weekMappers.forEach((t)->t.getGroup().setStandByDeivces(childGroupDevices));


        Map<Long, List<WeekMapper>> grouppingWeekMapper = weekMappers.stream()
                .collect(groupingBy(weekMapper -> weekMapper.getSchedule().getId()));
        rootGroup.forEach(t->t.getSchedule().setWeeks(grouppingWeekMapper.get(t.getSchedule().getId())));


//        List<Long> weekMapperIds = weekMappers.stream()
//                .map((t) -> t.getId())
//                .collect(toList());
//        List<Group> weekGroups = getGroupByMapperIdAndParentGroupId(weekMapperIds, parentGroupIds);




        return rootGroup;

    }
}
