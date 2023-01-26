package com.markcha.scheduler.repository.schedule.impl;

import com.markcha.scheduler.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.scheduler.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.scheduler.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.scheduler.domain.QGroup.group;
import static com.markcha.scheduler.domain.QSchedule.schedule;
import static com.markcha.scheduler.domain.QWeek.week;
import static com.markcha.scheduler.domain.QWeekMapper.weekMapper;

@Repository
public class ScheduleDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;


    public ScheduleDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }
    public Schedule getOneByGroupId(Long id) {
        return query.select(schedule)
                .from(schedule)
                .leftJoin(schedule.groups, group)
                .where(group.id.eq(id))
                .fetchOne();
    }
    public List<Schedule> getAll() {
        return query.select(schedule)
                .from(schedule)
                .where(schedule.isActive.eq(true))
                .fetch();
    }
    public List<Schedule> findAllCoreSchedule() {
        return query.selectFrom(schedule)
                .leftJoin(schedule.groups, group).fetchJoin()
                .where(group.isNotNull(),schedule.isActive.eq(true))
                .fetch();
    }
    public Group findRootGroupId(Long id) {
        return query.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .where(schedule.id.eq(id))
                .fetchOne();
    }
    public List<Schedule> findAllJoinMapper() {
        return query.selectFrom(schedule).distinct()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper)
                .leftJoin(weekMapper.week, week)
                .fetch();
    }
    public Schedule getOne(Long id) {
        return query.selectFrom(schedule).distinct()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper).fetchJoin()
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper)
                .leftJoin(weekMapper.week, week)
                .where(schedule.id.eq(id))
                .fetchOne();
    }
    public Schedule a(Long id) {
        return query.selectFrom(schedule)
                .where(schedule.id.eq(id))
                .fetchOne();
    }
}
