package com.markcha.ems.repository.equipment.impl;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.QEquipment;
import com.markcha.ems.repository.equipment.EquipmentRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.Objects;

import static com.markcha.ems.domain.QEquipment.equipment;
import static java.util.Objects.isNull;

@Repository
public class EquipmentDslRepositoryImpl implements EquipmentRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;


    public EquipmentDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    @Override
    public Equipment getOneByTypeAndModel(String type, String model) {
        BooleanExpression typeEq = null;
        BooleanExpression modelEq = null;
        if(!isNull(type)) {
            typeEq = equipment.type.eq(type);
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
    @Override
    public Equipment getOneById(Long id) {
        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        equipment.id.eq(id))
                .fetchOne();
    }
    @Override
    public Equipment getOneByType(String type) {
        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        equipment.type.eq(type))
                .fetchOne();
    }
}
