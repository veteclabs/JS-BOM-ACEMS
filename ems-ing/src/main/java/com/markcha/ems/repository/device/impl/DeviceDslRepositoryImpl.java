package com.markcha.ems.repository.device.impl;

import com.markcha.ems.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static com.markcha.ems.domain.EquipmentType.POWER_METER;
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


    public DeviceDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
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
    public List<Tag> getCompressorTags(List<Long> compIds) {
        return query.selectFrom(tag)
                .leftJoin(tag.device, device).fetchJoin()
                .where(
                         device.id.in(compIds)
                        ,tag.tagName.in(new ArrayList<>(List.of("PRESSURE_MIN", "PRESSURE_MAX")))
                ).fetch();
    }
    public List<Device> findAllCompressors(EquipmentType typeName) {
        List<Device> compressor = getCompressor(typeName);
        List<Long> compIds = compressor.stream()
                .map(t -> t.getId())
                .collect(toList());
        Map<Long, List<Tag>> grouppingTags = getCompressorTags(compIds).stream()
                .collect(groupingBy(t -> t.getDevice().getId()));

        compressor.forEach(t->{
            if(!isNull(grouppingTags.get(t.getId()))) {
                t.setTags(new HashSet<>(grouppingTags.get(t.getId())));
            }
        });
        return compressor;
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
                        ,device.id.in(ids)
                )
                .orderBy(device.id.desc())
                .fetch();
    }
    public List<Device> findAllCompressorsJoinEquipment(EquipmentType typeName) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");

        return query.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, childGroup).fetchJoin()
                .leftJoin(childGroup.parent, parentGroup).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(childGroup.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(equipment.type.eq(typeName))
                .orderBy(childGroup.id.desc())
                .fetch();
    }
    public Device getOneCompressorsJoinEquipment(Long id, EquipmentType typeName) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");
        return query.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.group, childGroup).fetchJoin()
                .leftJoin(childGroup.parent, parentGroup).fetchJoin()
                .leftJoin(childGroup.schedule, schedule).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         equipment.type.eq(typeName)
                        ,childGroup.id.eq(id)
                )
                .fetchOne();
    }
//    public List<Group> getParentGroup(List<>)
    public List<Device> findAllEtcOrphs() {
        return query.selectFrom(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         device.group.isNull()
                        ,equipment.type.ne(AIR_COMPRESSOR)
                ).fetch();
    }
    public List<Device> findAllAirOrphs() {
        QGroup parentGroup = new QGroup("pg");
        return query.selectFrom(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                        parentGroup.isNull(),
                        group.type.eq(OBJECT)
                ).fetch();
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
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .where(
                        device.id.eq(id)
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
}
