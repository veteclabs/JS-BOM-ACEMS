package com.markcha.ems.repository.order;

import com.markcha.ems.domain.QOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QOrder.order1;

@Repository
@Transactional
public class OrderDslRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;

    public OrderDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }
    public long deleteByIds(List<Long> ids) {
        return query.delete(order1).where(order1.id.in(ids)).execute();
    }

}
