package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEnergy is a Querydsl query type for Energy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEnergy extends EntityPathBase<Energy> {

    private static final long serialVersionUID = 453595011L;

    public static final QEnergy energy = new QEnergy("energy");

    public final SetPath<Device, QDevice> devices = this.<Device, QDevice>createSet("devices", Device.class, QDevice.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> kcal = createNumber("kcal", Double.class);

    public final NumberPath<Double> MJ = createNumber("MJ", Double.class);

    public final StringPath name = createString("name");

    public final NumberPath<Double> tCO2 = createNumber("tCO2", Double.class);

    public final NumberPath<Double> toe = createNumber("toe", Double.class);

    public final NumberPath<Integer> usage = createNumber("usage", Integer.class);

    public QEnergy(String variable) {
        super(Energy.class, forVariable(variable));
    }

    public QEnergy(Path<? extends Energy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEnergy(PathMetadata metadata) {
        super(Energy.class, metadata);
    }

}

