package com.markcha.ems.repository.equipment.impl;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.QEquipment;
import com.markcha.ems.repository.equipment.EquipmentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.markcha.ems.domain.QEquipment.equipment;

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
        return queryFactory.select(equipment)
                .from(equipment)
                .where(
                        equipment.type.eq(type),
                        equipment.model.eq(model))
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
