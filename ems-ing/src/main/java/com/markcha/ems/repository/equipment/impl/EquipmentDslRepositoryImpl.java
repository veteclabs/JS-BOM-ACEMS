package com.markcha.ems.repository.equipment.impl;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.dto.device.CompressorModelDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static com.markcha.ems.domain.QEquipment.equipment;
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

    public List<String> getMakers() {
        return queryFactory.select(equipment.maker)
                .distinct()
                .from(equipment)
                .where(equipment.maker.isNotNull(),
                        equipment.type.eq(EquipmentType.AIR_COMPRESSOR))
                .fetch();
    }

    public List<CompressorModelDto> getModels(String maker) {
        return queryFactory.select(
                constructor(CompressorModelDto.class, equipment.id, equipment.model))
                .distinct()
                .from(equipment)
                .where(equipment.model.isNotNull(),
                        equipment.type.eq(EquipmentType.AIR_COMPRESSOR),
                        equipment.maker.eq(maker)
                )
                .fetch();
    }
}
