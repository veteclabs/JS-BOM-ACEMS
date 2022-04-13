package com.markcha.ems.repository.schedule.impl;

import com.markcha.ems.domain.QGroup;
import com.markcha.ems.domain.QSchedule;
import com.markcha.ems.domain.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QSchedule.schedule;

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

    public List<Schedule> findAll() {
        return query.select(schedule)
                .from(schedule)
                .fetch();
    }
}
