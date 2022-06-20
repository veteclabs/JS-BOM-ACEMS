package com.markcha.ems.repository.group.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.controller.GroupController;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static com.markcha.ems.domain.EquipmentType.PRESSURE_GAUGE;
import static com.markcha.ems.domain.GroupType.OBJECT;
import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QOrder.order1;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QTag.tag;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
import static java.util.Comparator.comparing;
import static java.util.Objects.compare;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

@Repository
public class GroupDslRepositoryImpl{
    private final EntityManager entityManager;
    private final JPAQueryFactory query;
    private final WebaccessApiServiceImpl webaccessApiService;
    public Group getOneJoinScheduleById(Long id) {
        BooleanExpression groupEqId = null;
        if(!isNull(id)) {
            groupEqId = group.id.eq(id);
        } else {
            groupEqId = group.id.eq(-1L);
        }
        return query.selectFrom(group)
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .where(groupEqId)
                .fetchOne();
    }
    public GroupDslRepositoryImpl(EntityManager entityManager, WebaccessApiServiceImpl webaccessApiService) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
        this.webaccessApiService = webaccessApiService;
    }

    public long countPressure(Long groupId) {
        return query.select(device).distinct()
                .from(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         equipment.type.eq(PRESSURE_GAUGE)
                        ,group.id.eq(groupId)
                ).fetchCount();
    }
    public List<Group> findAllByType(GroupType type) {
        return query.select(group)
                .from(group)
                .where(
                        group.type.eq(type)
                )
                .orderBy(group.id.desc())
                .fetch();
    }
    public List<DayOfWeek> findAllByGroupId(Long id) {
        return query.selectFrom(dayOfWeek)
                .leftJoin(dayOfWeek.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.schedule, schedule).fetchJoin()
                .leftJoin(schedule.groups, group).fetchJoin()
                .where(group.id.eq(id))
                .fetch();
    }
    
    public Group getOneById(Long id) {
        BooleanExpression groupEqId = null;
        if(!isNull(id)) {
            groupEqId = group.id.eq(id);
        } else {
            groupEqId = group.id.eq(-1L);
        }
        return query.selectFrom(group)
                .where(groupEqId)
                .limit(1)
                .fetchOne();
    }
    public Group getOneJoinChildsAndDevicesById(Long id) {
        QGroup childGroup = new QGroup("cg");
        Group groupEntity = query.selectFrom(group)
                .leftJoin(group.children, childGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(group.id.eq(id))
                .fetchOne();
        List<Device> devices = query.selectFrom(device)
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(equipment.type.ne(AIR_COMPRESSOR), device.group.id.eq(groupEntity.getId()))
                .fetch();
        groupEntity.setDeviceSet(new HashSet<>(devices));
        return groupEntity;
    }
    public Group findAllBroGroupByBroId(Long id) {
        QGroup childGroup = new QGroup("cg");
        QGroup parentGroup = new QGroup("pg");
        QGroup broGroup = new QGroup("bg");
        return query.select(parentGroup)
                .from(childGroup)
                .leftJoin(childGroup.parent, parentGroup)
                .leftJoin(parentGroup.children, broGroup).fetchJoin()
                .where(childGroup.id.eq(id))
                .fetchOne();
    }
    public Group getOneByDeviceId(Long id) {
        return query.select(group)
                .from(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .where(device.id.eq(id))
                .fetchOne();
    }

    private List<WeekMapper> getWeekMapper(List<Long> scheduleIds) {
        return query.select(weekMapper)
                .from(weekMapper).distinct()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(weekMapper.schedule, schedule).fetchJoin()
                .where(
                         schedule.id.in(scheduleIds)
                )
                .fetch();
    }
    private List<Order> getOrderGroups(List<Long> weekMapperIds) {
        return query.selectFrom(order1).distinct()
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(order1.weekMapper, weekMapper).fetchJoin()
                .where(
                         weekMapper.id.in(weekMapperIds)
//                        ,group.type.eq(GroupType.OBJECT)
                ).fetch();
    }
    private List<Group> getRootGroups(BooleanExpression findById) {
        QGroup childGroup = new QGroup("cg");
        return query.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(group.children, childGroup).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(
                         group.type.eq(GroupType.GROUP)
                        ,findById
                )
                .orderBy(group.id.asc())
                .fetch();
    }
    public List<Group> findAllJoinScheduleByScheduleId(Long id) {
        List<Group> groups = query.selectFrom(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                        group.schedule.id.eq(id)
                ).fetch();
        List<Long> groupIds = groups.stream()
                .map(t -> t.getId())
                .collect(toList());
        List<Device> devicesList = query.selectFrom(device)
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                        device.group.id.in(groupIds)
                        , tag.type.eq("AIR_PRE")
                        ,equipment.type.eq(PRESSURE_GAUGE)
                ).fetch();
        Device devices = null;
        if(!isNull(devicesList) && devicesList.size() > 0) devices = devicesList.get(0);

        // webaccess 태그 이름으로 조회 //
        if(!isNull(devices)) {
            List<String> tagNames = devices.getTags().stream()
                    .map(k -> k.getTagName())
                    .collect(toList());
            Map<String, Object> tagValues = webaccessApiService.getTagValuesV2(tagNames);
            if(!isNull(devices)) {
                Device finalDevices = devices;
                devices.getTags().forEach(k -> {
                    finalDevices.setPressure(tagValues.get(k.getTagName()));
                });
                Device finalDevices1 = devices;
                groups.forEach(t->t.setTagetDevice(finalDevices1));
            }

        }


        return groups;
    }
    private Group getRootGroup(BooleanExpression findById) {
        QGroup childGroup = new QGroup("childGroup");
        return query.select(group).distinct()
                .from(group)
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(group.children ,childGroup).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(
                        group.type.eq(GroupType.GROUP)
                        ,findById
                ).fetchOne();
    }
    private List<Group> getGroups() {
        QGroup childGroup = new QGroup("cg");
        QDevice childDevice = new QDevice("cd");
        QTag childTag = new QTag("ct");
        return query.selectFrom(group).distinct()
                .leftJoin(group.children, childGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(childGroup.deviceSet, childDevice).fetchJoin()
                .where(group.type.eq(GroupType.GROUP))
                .orderBy(group.id.desc())
                .fetch();
    }
    public List<Group> getGroups(List<Long> ids) {
        return query.selectFrom(group)
                .where(group.id.in(ids))
                .fetch();
    }
    public List<Tag> getTagsByDeviceIds(List<Long> deviceIds) {
        return query.selectFrom(tag).distinct()
                .join(tag.device, device).fetchJoin()
                .where(
                         tag.showAble.eq(true)
                        ,device.id.in(deviceIds))
                .fetch();
    }
    public List<Tag> getTagsByDeviceIdsOnlyBar(List<Long> deviceIds) {
        return query.selectFrom(tag).distinct()
                .join(tag.device, device).fetchJoin()
                .where(
                         tag.showAble.eq(true)
                        ,tag.type.eq("AIR_PRE")
                        ,device.id.in(deviceIds))
                .fetch();
    }
    public List<Group> findAllGroupJoinTags() {
        List<Group> groups = getGroups();
        List<Long> deviceIds = new ArrayList<>();
        groups.forEach(g->{
            g.getChildren().forEach(c->c.getDeviceSet().forEach(d->deviceIds.add(d.getId())));
            g.getDeviceSet().forEach(d->deviceIds.add(d.getId()));
        });
        List<Tag> tags = getTagsByDeviceIds(deviceIds);
        // webaccess 태그 이름으로 조회 //
        List<String> tagNames = tags.stream()
                .map(k -> k.getTagName())
                .collect(toList());

        Map<String, Object> tagValues = webaccessApiService.getTagValuesV2(tagNames);

        tags.forEach(a -> {
            a.setValue(tagValues.get(a.getTagName()));
        });

        Map<Long, List<Tag>> groupByTag = tags.stream()
                .collect(groupingBy(t -> t.getDevice().getId()));
        groups.forEach(g->{
            g.getDeviceSet().forEach(d->{
                if(!isNull(groupByTag.get(d.getId()))) d.setTags(new HashSet<>(groupByTag.get(d.getId())));
            });
            g.getChildren().forEach(c->c.getDeviceSet().forEach(d->{
                    if (!isNull(groupByTag.get(d.getId()))) {
                        d.setTags(new HashSet<>(groupByTag.get(d.getId())));
                    }
                }));
        });
        // -----------------------------------//
        return groups;
    }
    public List<Group> getChildGroups(List<Long> parentGroupIds) {
        QGroup parentGroup = new QGroup("cg");
        return query.selectFrom(group).distinct()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
//                .leftJoin(device.tags, tag).fetchJoin()
                .where(
                         parentGroup.id.in(parentGroupIds)
                        ,group.type.eq(OBJECT)
//                        ,equipment.type.eq(AIR_COMPRESSOR)
                )
                .fetch();
    }
    
    public List<Group> findAllChildGroupsById(Long id) {
        QGroup parentGroup = new QGroup("parentGroup");
        return query.selectFrom(group)
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(parentGroup.id.eq(id))
                .fetch();
    }

    public List<Tag> findAllChildGruopMaxTags(Long parentId) {
        QGroup parentGroup = new QGroup("parentGroup");
        return query.selectFrom(tag).distinct()
                .leftJoin(tag.device, device).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .where(
                         parentGroup.id.eq(parentId)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                        ,tag.type.in("COMP_StopPre")
                ).fetch();
    }
    public List<Tag> findAllChildGruopMinTags(Long parentId) {
        QGroup parentGroup = new QGroup("parentGroup");
        return query.selectFrom(tag).distinct()
                .leftJoin(tag.device, device).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .where(
                        parentGroup.id.eq(parentId)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                        ,tag.type.in("COMP_StartPre")
                ).fetch();
    }

    public List<Group> findAllByIds(List<Long> ids) {
        BooleanExpression groupIdEq = null;
        if(!isNull(ids)) {
            groupIdEq = group.id.in(ids);
        } else {
            groupIdEq = group.id.eq(-1L);
        }
        return query.selectFrom(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
//                .leftJoin(device.equipment, equipment).fetchJoin()
//                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(groupIdEq)
                .fetch();
    }
    
    public List<Group> findAllJoinSchedule() {
        List<Group> rootGroup = getRootGroups(null);

//        List<Long> scheduleIds = rootGroup.stream()
//                .map((t) -> t.getSchedule().getId())
//                .collect(toList());
//
//        List<Long> parentGroupIds = rootGroup.stream()
//                .map((t) -> t.getId())
//                .collect(toList());
//        List<WeekMapper> weekMappers = getWeekMapper(scheduleIds);
//        List<Long> weekMapperIds = weekMappers.stream()
//                .map(t -> t.getId())
//                .collect(toList());
//        Map<Long, List<Order>> groupingOrder = getOrderGroups(weekMapperIds).stream()
//                .collect(groupingBy(t -> t.getWeekMapper().getId()));
//
//        weekMappers.forEach(t->{
//            if(!isNull(groupingOrder.get(t.getId()))) {
//                t.setOrders(new HashSet<>(groupingOrder.get(t.getId())));
//            }
//        });
//
//        List<Group> childGroups = null;
//        if(!isNull(rootGroup.get())) {
//            childGroups = getChildGroups(parentGroupIds);
//        }
//
//        Map<Long, List<Group>> groupByParentId = childGroups.stream()
//                .collect(groupingBy(t -> t.getParent().getId()));
//        for (WeekMapper mapper : weekMappers) {
//            List<Group> childGroupCopy = new ArrayList<>();
//            childGroupCopy.addAll(childGroups);
//            List<Group> workingGroups = mapper.getOrders().stream()
//                    .map(t -> t.getGroup())
//                    .collect(toList());
//            childGroupCopy.removeAll(workingGroups);
////            childGroupCopy.forEach(t-> System.out.println(t.getName()));
//            mapper.setStandByGroups(childGroupCopy);
//        }
//
//        Map<Long, List<WeekMapper>> grouppingWeekMapper = weekMappers.stream()
//                .collect(groupingBy(weekMapper -> weekMapper.getSchedule().getId()));
//        rootGroup.forEach(t->{
//            Long scheduleId = t.getSchedule().getId();
//            List<WeekMapper> weekMappers1 = grouppingWeekMapper.get(scheduleId);
//            if (!isNull(t.getSchedule())) {
//                t.getSchedule();
//                t.getSchedule().setWeeks(weekMappers1);
//            }
//        });
        return rootGroup;

    }
    
    public Group getOneJoinSchedule(Long id) {
        Group rootGroup = getRootGroup(group.id.eq(id));

        List<Long> scheduleIds = new ArrayList<>(List.of(rootGroup.getSchedule().getId()));
        List<Long> parentGroupIds = new ArrayList<>(List.of(rootGroup.getId()));
        List<WeekMapper> weekMappers = getWeekMapper(scheduleIds);
        List<Long> weekMapperIds = weekMappers.stream()
                .map(t -> t.getId())
                .collect(toList());
        Map<Long, List<Order>> groupingOrder = getOrderGroups(weekMapperIds).stream()
                .collect(groupingBy(t -> t.getWeekMapper().getId()));

        weekMappers.forEach(t->{
            if(!isNull(groupingOrder.get(t.getId()))) {
                t.setOrders(new HashSet<>(groupingOrder.get(t.getId())));
            }
        });
        List<Group> childGroups = null;
        if(!isNull(rootGroup.getChildren())) {
            childGroups = getChildGroups(parentGroupIds);
        }

        Map<Long, List<Group>> groupByParentId = childGroups.stream()
                .collect(groupingBy(t -> t.getParent().getId()));
        for (WeekMapper mapper : weekMappers) {
            List<Group> childGroupCopy = new ArrayList<>();
            childGroupCopy.addAll(childGroups);
            List<Group> workingGroups = mapper.getOrders().stream()
                    .map(t -> t.getGroup())
                    .collect(toList());
            childGroupCopy.removeAll(workingGroups);
//            childGroupCopy.forEach(t-> System.out.println(t.getName()));
            mapper.setStandByGroups(childGroupCopy);
        }

        Map<Long, List<WeekMapper>> grouppingWeekMapper = weekMappers.stream()
                .collect(groupingBy(weekMapper -> weekMapper.getSchedule().getId()));
        rootGroup.getSchedule().setWeeks(grouppingWeekMapper.get(rootGroup.getSchedule().getId()));
        return rootGroup;

    }

}
