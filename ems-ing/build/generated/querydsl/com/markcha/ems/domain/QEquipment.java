package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEquipment is a Querydsl query type for Equipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEquipment extends EntityPathBase<Equipment> {

    private static final long serialVersionUID = -1214094669L;

    public static final QEquipment equipment = new QEquipment("equipment");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final SetPath<Device, QDevice> devices = this.<Device, QDevice>createSet("devices", Device.class, QDevice.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath maker = createString("maker");

    public final StringPath model = createString("model");

    public final StringPath name = createString("name");

    public final SetPath<TagList, QTagList> tagLists = this.<TagList, QTagList>createSet("tagLists", TagList.class, QTagList.class, PathInits.DIRECT2);

    public final EnumPath<EquipmentType> type = createEnum("type", EquipmentType.class);

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public QEquipment(String variable) {
        super(Equipment.class, forVariable(variable));
    }

    public QEquipment(Path<? extends Equipment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEquipment(PathMetadata metadata) {
        super(Equipment.class, metadata);
    }

}

