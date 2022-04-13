package com.markcha.ems.repository.device.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.device.DeviceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
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
        return query.select(device)
                .from(device)
                .join(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(equipment.type.ne(typeName))
                .orderBy(device.id.desc())
                .fetch();
    }

    public List<Device> findAllCompressors(EquipmentType typeName) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");
        return query.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, childGroup).fetchJoin()
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
                        group.type.eq("compressor")
                ).fetch();
    }
    public Device getOneById(Long id) {
        return query.select(device)
                .from(device)
                .where(
                        device.id.eq(id)
                ).fetchOne();
    }
    
    public Device getOneByIdJoinGroupSchedule(Long id) {
        return query.select(device)
                .from(device)
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(
                        device.id.eq(id)
                ).limit(1).fetchOne();
    }
    public List<Device> findAllByIds(List<Long> ids) {
        return query.selectFrom(device)
                .where(device.id.in(ids)).fetch();
    }
}
