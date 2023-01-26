package com.markcha.ems.repository.dayofweekmapper.impl;

import com.markcha.ems.domain.DayOfWeekMapper;
import com.markcha.ems.domain.QDayOfWeekMapper;
import com.markcha.ems.domain.QSchedule;
import com.markcha.ems.repository.DayOfWeekMapperDataRepository;
import com.markcha.ems.repository.dayofweekmapper.DayOfWeekMapperRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.querydsl.core.types.Projections.constructor;

@Repository
public class DayOfWeekMapperDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;


    public DayOfWeekMapperDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }

    public List<Long> findAllByScheduleId(Long id) {
        return query.select(
                    constructor(
                        Long.class,
                        dayOfWeekMapper.id))
                .from(dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.schedule, schedule)
                .where(schedule.id.eq(id))
                .fetch();
    }
    @Transactional
    public Boolean deleteByIdIn(List<Long> ids) {
        query.delete(dayOfWeekMapper)
                .where(dayOfWeekMapper.id.in(ids))
                .execute();
        return true;
    }
}
