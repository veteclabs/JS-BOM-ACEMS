package com.markcha.ems.repository.device.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.markcha.ems.domain.EquipmentType.*;
import static com.markcha.ems.domain.GroupType.GROUP;
import static com.markcha.ems.domain.GroupType.OBJECT;
import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QTag.tag;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Repository
public class DeviceDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;
    private final WebaccessApiServiceImpl webaccessApiService;

    public DeviceDslRepositoryImpl(EntityManager entityManager, WebaccessApiServiceImpl webaccessApiService) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
        this.webaccessApiService = webaccessApiService;
    }

    public List<Device> findAllTemplcates(EquipmentType typeName) {
        return query.select(device).distinct()
                .from(device)
                .join(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .where(equipment.type.ne(typeName))
                .orderBy(device.id.desc())
                .fetch();
    }
    public List<Device> findAllByType(String type) {
        EquipmentType equipment = null;
        switch (type) {
            case "FLOW":
                equipment = EquipmentType.FLOW_METER;
                break;
            case "KWH":
                equipment = POWER_METER;
                break;
        };
        assert equipment != null;
        return query.selectFrom(QDevice.device)
                .leftJoin(QDevice.device.equipment, QEquipment.equipment).fetchJoin()
                .where(QEquipment.equipment.type.eq(equipment))
                .fetch();

    }
    public long countingCompressorHavePowerDevice() {
        return query.selectFrom(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment)
                .where(
                         equipment.type.eq(POWER_METER)
                        ,group.type.eq(OBJECT)
                ).fetchCount();
    }
    public long countingGroupHavePressureDevice(Long groupId) {
        BooleanExpression groupEqId = null;
        if(isNull(groupId)) {
            groupEqId = group.id.eq(-1L);
        } else {
            groupEqId = group.id.eq(groupId);
        }
        return query.select(device).distinct()
                .from(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         equipment.type.eq(PRESSURE_GAUGE)
                        ,group.type.eq(GROUP)
                        ,groupEqId
                ).fetchCount();
    }
    public List<Device> getCompressor(EquipmentType typeName) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");
        return query.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, childGroup).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(childGroup.parent, parentGroup).fetchJoin()
                .leftJoin(childGroup.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                        equipment.type.eq(typeName)

                )
                .orderBy(device.id.desc())
                .fetch();
    }
    public List<Tag> getCompressorTags(List<Long> compIds, BooleanExpression tagEqIsAlarm) {
        return query.selectFrom(tag)
                .leftJoin(tag.device, device).fetchJoin()
                .where(
                         device.id.in(compIds)
                        ,tagEqIsAlarm
                ).fetch();
    }
    public List<Device> findAllCompressors(EquipmentType typeName, BooleanExpression tagEqIsAlarm) {
        List<Device> compressor = getCompressor(typeName);
        List<Long> compIds = compressor.stream()
                .map(t -> t.getId())
                .collect(toList());
        List<Tag> tags = getCompressorTags(compIds,tagEqIsAlarm);
        Map<String, Object> tagValues = webaccessApiService.getTagValuesV2(tags.stream()
                .map(t -> t.getTagName()).collect(toList()));
        tags.forEach(t->t.setValue(tagValues.get(t.getTagName())));
        Map<Long, List<Tag>> grouppingTags = tags.stream()
                .collect(groupingBy(t -> t.getDevice().getId()));

        compressor.forEach(t->{
            if(!isNull(grouppingTags.get(t.getId()))) {
                t.setTags(new HashSet<>(grouppingTags.get(t.getId())));
            }
        });

        return compressor;
    }
    public List<Tag> findAllAlarmTags() {
        List<Tag> tags = getAlarmTags();

        Map<Long, List<Device>> alarmGroupDevices = getAlarmGroupDevices(tags.stream()
                .map(t -> {

                    return t.getDevice().getGroup().getId();
                })
                .collect(toList())).stream()
                .collect(groupingBy(t->t.getGroup().getId(), toList()));
        tags.forEach(t->{
            if(!isNull(alarmGroupDevices.get(t.getDevice().getGroup().getId()))) {
                Group group = t.getDevice().getGroup();
                group.setDevices(new HashSet<>(alarmGroupDevices.get(t.getDevice().getGroup().getId())));
            }
        });
        List<String> tagNames = new ArrayList<>();
        tagNames.addAll(tags.stream()
                .map(t -> t.getTagName()).collect(toList()));
        tagNames.addAll(tags.stream()
                .map(t -> t.getDevice().getGroup().getDevices().stream()
                        .map(k -> {
                            return k.getTags().stream()
                                    .map(Tag::getTagName)
                                    .collect(toList());
                        }).collect(toList()))
                .flatMap(List::stream)
                .flatMap(List::stream)
                .collect(toList()));


        Map<String, Object> tagValues = webaccessApiService.getTagValuesV2(tagNames);
        tags.forEach(t->{
            t.setValue(tagValues.get(t.getTagName()));
            t.getDevice().getGroup().getDevices().forEach(k->{
                k.getTags().forEach(g->g.setValue(tagValues.get(g.getTagName())));
            });
        });
        return tags;
    }
    public List<Tag> getAlarmTags() {
        QTag compTags = new QTag("compTags");
        return query.selectFrom(tag).distinct()
                .leftJoin(tag.device, device).fetchJoin()
                .leftJoin(device.tags, compTags).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(
                         equipment.type.eq(AIR_COMPRESSOR)
                ).fetch();
    }
    public List<Device> getAlarmGroupDevices(List<Long> groupIds) {
        return query.selectFrom(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .where(
                         group.id.in(groupIds)
                        ,equipment.type.eq(POWER_METER)
                ).fetch();
    }
    public List<Device> findAllCompressorsByIds(EquipmentType typeName, List<Long> ids) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");
        QDevice orphanDevice = new QDevice("orphanDevice");
        return query.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, childGroup).fetchJoin()
                .leftJoin(childGroup.deviceSet, orphanDevice).fetchJoin()
                .leftJoin(childGroup.parent, parentGroup).fetchJoin()
                .leftJoin(childGroup.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         equipment.type.eq(typeName)
                        ,childGroup.id.in(ids)
                )
                .orderBy(device.id.desc())
                .fetch();
    }



    public List<Group> findAllCompressorsJoinEquipment(EquipmentType typeName, BooleanExpression groupEqId) {
        QGroup parentGroup = new QGroup("pGroup");
        return query.select(group)
                .from(group).distinct()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         equipment.type.eq(typeName)
                        ,groupEqId
                ).orderBy(group.id.desc())
                .fetch();
    }
    public List<Device> findAllEtcOrphs() {
        return query.selectFrom(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         group.isNull()
                        ,equipment.type.ne(AIR_COMPRESSOR)
                ).fetch();
    }
    public List<Device> findAllAirOrphs() {
        QGroup parentGroup = new QGroup("pg");
        QDevice groupDevice = new QDevice("gd");
        QEquipment groupEquipment = new QEquipment("groupEquipment");


        List<Device> comp = query.selectFrom(device).distinct()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         parentGroup.isNull()
                        ,group.type.eq(OBJECT)
                ).fetch();
        List<Long> groupIds = comp.stream()
                .map(t -> t.getGroup().getId())
                .collect(toList());
        List<Device> devices = query.selectFrom(device).distinct()
                .leftJoin(device.group, group).fetchJoin()
                .innerJoin(device.equipment, equipment).fetchJoin()
                .where(
                        equipment.type.ne(AIR_COMPRESSOR)
                        ,group.id.in(groupIds)
                ).fetch();
        Map<Long, List<Device>> goupingByDevice = devices.stream()
                .collect(groupingBy(t -> t.getGroup().getId(), toList()));
        comp.forEach(t->{
            if(!isNull(goupingByDevice.get(t.getId())))
                t.getGroup().setDeviceSet(new HashSet<>(goupingByDevice.get(t.getId())));
        });
        return comp;
    }
    public Device getOneById(Long id) {
        return query.select(device)
                .from(device)
                .where(
                        device.id.eq(id)
                ).fetchOne();
    }
    public List<Device> getDeviceByGroupIds(List<Long> groupIds) {
        return query.selectFrom(device).distinct()
                .join(device.tags, tag).fetchJoin()
                .join(device.group, group).fetchJoin()
                .where(
                        tag.showAble.eq(true)
                        ,group.id.in(groupIds)
                ).fetch();
    }
    public Device getOneByIdJoinGroupSchedule(Long id) {
        return query.select(device)
                .from(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(
                         group.id.eq(id)
                        ,equipment.type.eq(AIR_COMPRESSOR)
                ).fetchOne();
    }
    public List<Device> findAllByIds(List<Long> ids) {
        BooleanExpression deviceIdEq = null;
        if(!isNull(ids)) {
            deviceIdEq = device.id.in(ids);
        } else {
            deviceIdEq = device.id.eq(-1L);
        }
        return query.selectFrom(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .where(deviceIdEq).fetch();
    }
    public List<Device> findAllJoinGroupByIds(List<Long> ids) {
        BooleanExpression deviceIdEq = null;
        if(!isNull(ids)) {
            deviceIdEq = device.id.in(ids);
        } else {
            deviceIdEq = device.id.eq(-1L);
        }
        return query.selectFrom(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .where(deviceIdEq).fetch();
    }
    public List<Tag> findAllByTagTypeAndIdsV2(BooleanExpression deviceEqId, BooleanExpression tagEqType) {
        return query.select(tag).distinct()
                .from(tag)
                .leftJoin(tag.device, device).fetchJoin()
                .where(deviceEqId, tagEqType).fetch();
    }
}
