package com.markcha.ems.repository.group.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.controller.GroupController;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
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
        return query.select(device)
                .from(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         equipment.type.eq(EquipmentType.PRESSURE_GAUGE)
                        ,group.id.eq(groupId)
                )
                .fetchCount();
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
        Group group = query.selectFrom(QGroup.group)
                .leftJoin(QGroup.group.children, childGroup).fetchJoin()
                .leftJoin(QGroup.group.deviceSet, device).fetchJoin()
                .leftJoin(QGroup.group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(QGroup.group.id.eq(id))
                .fetchOne();
        List<Device> devices = query.selectFrom(QDevice.device)
                .leftJoin(QDevice.device.equipment, equipment).fetchJoin()
                .leftJoin(QDevice.device.group, QGroup.group).fetchJoin()
                .where(equipment.type.ne(AIR_COMPRESSOR), device.group.id.eq(group.getId()))
                .fetch();
        group.setDeviceSet(new HashSet<>(devices));
        return group;
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
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device)
                .leftJoin(device.equipment, equipment)
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         weekMapper.schedule.id.in(scheduleIds)
                        ,equipment.type.eq(AIR_COMPRESSOR).or(
                                order1.isNull()
                        )
                )
                .fetch();
    }
    private List<Group> getRootGroups(BooleanExpression findById) {
        return query.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
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
        List<Device> devices = query.selectFrom(device)
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(
                        device.group.id.in(groupIds)
                        , tag.type.eq("BAR")
                ).fetch();

        List<Long> deviceIds = devices.stream()
                .map(t->t.getId())
                .collect(toList());

        List<TagDto> tags = getTagsByDeviceIdsOnlyBar(deviceIds).stream()
                .map(TagDto::new)
                .collect(toList());
        // webaccess 태그 이름으로 조회 //
        List<String> tagNames = tags.stream()
                .map(k -> k.getTagName())
                .collect(toList());
        try {
            List<JsonNode> tagValues = webaccessApiService.getTagValues(tagNames);

            tags.forEach(a -> {
                List<JsonNode> tags2 = tagValues.stream()
                        .filter(l -> a.getTagName().equals(l.get("Name").toString().replace("\"", "")))
                        .collect(toList());
                if(!isNull(tags2) && tags2.size() == 1) {
                    a.setValue(tags2.get(0).get("Value").doubleValue());
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        devices.forEach(t-> {
            tags.forEach(k->{
                if(k.getDeviceId().equals(t.getId())) {
                    t.setPressure(k);
                }
            });
        });
        groups.forEach(t-> {
            devices.forEach(k->{
                if(k.getGroup().getId().equals(t.getId())) {
                    t.setTagetDevice(k);
                }
            });
        });

        return groups;
    }
    private Group getRootGroup(BooleanExpression findById) {
        return query.select(group)
                .from(group)
                .leftJoin(group.schedule, schedule).fetchJoin()
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
                .leftJoin(device.tags, tag)
                .leftJoin(childGroup.deviceSet, childDevice)
                .leftJoin(childDevice.tags, childTag)
                .leftJoin(childGroup.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
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
                        ,tag.type.eq("BAR")
                        ,device.id.in(deviceIds))
                .fetch();
    }
    public List<Group> findAllGroupJoinTags() throws JsonProcessingException {
        List<Group> groups = getGroups();
        List<Long> deviceIds = new ArrayList<>();
        groups.forEach(g->{
            g.getChildren().forEach(c->c.getDeviceSet().forEach(d->deviceIds.add(d.getId())));
            g.getDeviceSet().forEach(d->deviceIds.add(d.getId()));
        });
        List<TagDto> tags = getTagsByDeviceIds(deviceIds).stream()
                .map(TagDto::new)
                .collect(toList());
        // webaccess 태그 이름으로 조회 //
        List<String> tagNames = tags.stream()
                .map(k -> k.getTagName())
                .collect(toList());
        try {
            List<JsonNode> tagValues = webaccessApiService.getTagValues(tagNames);

            tags.forEach(a -> {
                List<JsonNode> tags2 = tagValues.stream()
                        .filter(l -> a.getTagName().equals(l.get("Name").toString().replace("\"", "")))
                        .collect(toList());
                if(!isNull(tags2) && tags2.size() == 1) {
                    a.setValue(tags2.get(0).get("Value").doubleValue());
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map<Long, List<TagDto>> groupByTag = tags.stream()
                .collect(groupingBy(t -> t.getDeviceId()));
        groups.forEach(g->{
            g.getDeviceSet().forEach(d->d.setTagList(groupByTag.get(d.getId())));
            g.getChildren().forEach(c->c.getDeviceSet().forEach(d->{
                    d.setTagList(groupByTag.get(d.getId()));
                }));
        });
        // -----------------------------------//
        return groups;
    }
    private List<Group> getChildGroups(List<Long> parentGroupIds) {
        QGroup parentGroup = new QGroup("cg");
        return query.selectFrom(group)
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         parentGroup.id.in(parentGroupIds)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                )
                .fetch();
    }
    
    public List<Group> findAllChildGroupsById(Long id) {
        QGroup childGroup = new QGroup("cg");
        return query.selectFrom(group)
                .leftJoin(group.children, childGroup).fetchJoin()
                .fetch();
    }
    
    public List<Group> findAllByIds(List<Long> ids) {
        BooleanExpression groupIdEq = null;
        if(!isNull(ids)) {
            groupIdEq = group.id.in(ids);
        } else {
            groupIdEq = group.id.eq(-1L);
        }
        return query.selectFrom(group)
                .leftJoin(group.schedule, schedule).fetchJoin()
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
        rootGroup.forEach(t->{
            Long scheduleId = t.getSchedule().getId();
            List<WeekMapper> weekMappers1 = grouppingWeekMapper.get(scheduleId);
            if (!isNull(t.getSchedule())) {
                t.getSchedule();
                t.getSchedule().setWeeks(weekMappers1);
            }
        });
        return rootGroup;

    }
    
    public Group getOneJoinSchedule(Long id) {
        Group rootGroup = getRootGroup(group.id.eq(id));

        List<Long> scheduleIds = new ArrayList<>(List.of(rootGroup.getSchedule().getId()));
        List<Long> parentGroupIds = new ArrayList<>(List.of(rootGroup.getId()));
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
        rootGroup.getSchedule().setWeeks(grouppingWeekMapper.get(rootGroup.getSchedule().getId()));
        return rootGroup;

    }

}
