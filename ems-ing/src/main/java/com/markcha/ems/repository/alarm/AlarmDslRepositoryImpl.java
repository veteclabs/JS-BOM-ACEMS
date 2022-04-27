package com.markcha.ems.repository.alarm;

import com.markcha.ems.domain.*;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QAlarm.alarm;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QTag.tag;


@Repository
public class AlarmDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;
    private final WebaccessApiServiceImpl webaccessApiService;

    public AlarmDslRepositoryImpl(EntityManager entityManager, WebaccessApiServiceImpl webaccessApiService) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
        this.webaccessApiService = webaccessApiService;
    }
    public List<Alarm> findAllByGroupId(Long groupId) {
        return query.selectFrom(alarm)
                .leftJoin(alarm.tag, tag).fetchJoin()
                .leftJoin(tag.device, device).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(
                        group.id.eq(groupId)
                ).orderBy(alarm.id.desc()).fetch();
    }
}
