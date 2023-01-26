package com.markcha.scheduler.repository.equipment.impl;

import com.markcha.scheduler.domain.Equipment;
import com.markcha.scheduler.domain.EquipmentType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.scheduler.domain.QEquipment.equipment;
import static com.querydsl.core.types.Projections.constructor;
import static java.util.Objects.isNull;

@Repository
public class EquipmentDslRepositoryImpl  {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;


    public EquipmentDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    public Equipment getOneByTypeAndModel(String type, String model) {
        BooleanExpression typeEq = null;
        BooleanExpression modelEq = null;
        if(!isNull(type)) {
            typeEq = equipment.description.eq(type);
        }
        if(!isNull(model)) {
            modelEq = equipment.model.eq(model);
        }

        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        typeEq,
                        modelEq)
                .fetchOne();
    }
    public Equipment getOneById(Long id) {
        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        equipment.id.eq(id))
                .fetchOne();
    }
    public Equipment getOneByType(EquipmentType type) {
        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        equipment.type.eq(type))
                .limit(1)
                .fetchOne();
    }
    public List<EquipmentType> getTpyList() {
        return queryFactory.select(
                constructor(EquipmentType.class, equipment.type))
                .from(equipment)
                .fetch();
    }
}
