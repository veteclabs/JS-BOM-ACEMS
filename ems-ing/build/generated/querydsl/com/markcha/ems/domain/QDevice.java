package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDevice is a Querydsl query type for Device
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDevice extends EntityPathBase<Device> {

    private static final long serialVersionUID = 417151825L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDevice device = new QDevice("device");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final NumberPath<Double> ct = createNumber("ct", Double.class);

    public final QEnergy energy;

    public final QEnergyPurpose energyPurpose;

    public final QEquipment equipment;

    public final QGroup group;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Byte> importance = createNumber("importance", Byte.class);

    public final DateTimePath<java.time.LocalDateTime> installDtm = createDateTime("installDtm", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<Double> pt = createNumber("pt", Double.class);

    public final StringPath remark = createString("remark");

    public final StringPath SerialNumber = createString("SerialNumber");

    public final SetPath<Tag, QTag> tags = this.<Tag, QTag>createSet("tags", Tag.class, QTag.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public final EnumPath<Voltage> voltage = createEnum("voltage", Voltage.class);

    public QDevice(String variable) {
        this(Device.class, forVariable(variable), INITS);
    }

    public QDevice(Path<? extends Device> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDevice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDevice(PathMetadata metadata, PathInits inits) {
        this(Device.class, metadata, inits);
    }

    public QDevice(Class<? extends Device> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.energy = inits.isInitialized("energy") ? new QEnergy(forProperty("energy")) : null;
        this.energyPurpose = inits.isInitialized("energyPurpose") ? new QEnergyPurpose(forProperty("energyPurpose")) : null;
        this.equipment = inits.isInitialized("equipment") ? new QEquipment(forProperty("equipment")) : null;
        this.group = inits.isInitialized("group") ? new QGroup(forProperty("group"), inits.get("group")) : null;
    }

}

