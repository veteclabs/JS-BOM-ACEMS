package com.markcha.ems.repository.weekmapper.impl;

import com.markcha.ems.domain.QWeekMapper;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;

@Repository
public class WeekMapperDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;


    public WeekMapperDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }
    @Transactional
    public Boolean deleteByIdIn(List<Long> id) {
        query.delete(weekMapper)
                .where(weekMapper.id.in(id))
                .execute();
        return true;
    }
    public List<Long> findAllByScheduleId(Long id, BooleanExpression isNull) {
        return query.select(
                Projections.constructor(
                        Long.class,
                        weekMapper.id
                ))
                .from(weekMapper)
                .leftJoin(weekMapper.schedule, schedule)
                .where(schedule.id.eq(id), isNull)
                .fetch();
    }
}
