package com.markcha.ems.repository.device.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.device.DeviceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;


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
                .from(device)
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(equipment.type.eq(typeName))
                .fetch();
    }

    @Override
    public List<Device> findAllCompressors(String typeName) {
        return queryFactory.select(device)
                .from(device)
                .leftJoin(device.equipment, equipment).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .leftJoin(group.schedule, schedule).fetchJoin()

                .where(equipment.type.eq(typeName))
                .fetch();
    }
}
