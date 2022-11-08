package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEnergyPurpose is a Querydsl query type for EnergyPurpose
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEnergyPurpose extends EntityPathBase<EnergyPurpose> {

    private static final long serialVersionUID = 335436827L;

    public static final QEnergyPurpose energyPurpose = new QEnergyPurpose("energyPurpose");

    public final SetPath<Device, QDevice> equipmentMgmts = this.<Device, QDevice>createSet("equipmentMgmts", Device.class, QDevice.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QEnergyPurpose(String variable) {
        super(EnergyPurpose.class, forVariable(variable));
    }

    public QEnergyPurpose(Path<? extends EnergyPurpose> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEnergyPurpose(PathMetadata metadata) {
        super(EnergyPurpose.class, metadata);
    }

}

