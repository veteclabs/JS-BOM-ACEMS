package com.markcha.ems.repository.device.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.device.DeviceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

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
public class DeviceDslRepositoryImpl implements DeviceRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;


    public DeviceDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Device> findAllTemplcates(String typeName) {
        return queryFactory.select(device)
                .from(device).distinct()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(equipment.type.ne(typeName))
                .orderBy(device.id.asc())
                .fetch();
    }

    @Override
    public List<Device> findAllCompressors(String typeName) {
        QGroup parentGroup = new QGroup("pGroup");
        QGroup childGroup = new QGroup("cGroup");
        return queryFactory.select(device)
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
                .orderBy(device.id.asc())
                .fetch();
    }
    @Override
    public Device getOneById(Long id) {
        return queryFactory.select(device)
                .from(device)
                .where(
                        device.id.eq(id)
                ).fetchOne();
    }
}
