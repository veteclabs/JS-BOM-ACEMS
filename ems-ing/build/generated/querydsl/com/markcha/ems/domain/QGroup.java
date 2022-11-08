package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGroup is a Querydsl query type for Group
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroup extends EntityPathBase<Group> {

    private static final long serialVersionUID = 847892004L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGroup group = new QGroup("group1");

    public final SetPath<Link, QLink> childLocations = this.<Link, QLink>createSet("childLocations", Link.class, QLink.class, PathInits.DIRECT2);

    public final SetPath<Group, QGroup> children = this.<Group, QGroup>createSet("children", Group.class, QGroup.class, PathInits.DIRECT2);

    public final SetPath<Device, QDevice> deviceSet = this.<Device, QDevice>createSet("deviceSet", Device.class, QDevice.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath name = createString("name");

    public final SetPath<Order, QOrder> orders = this.<Order, QOrder>createSet("orders", Order.class, QOrder.class, PathInits.DIRECT2);

    public final QGroup parent;

    public final SetPath<Link, QLink> parentLocations = this.<Link, QLink>createSet("parentLocations", Link.class, QLink.class, PathInits.DIRECT2);

    public final StringPath purpose = createString("purpose");

    public final QSchedule schedule;

    public final EnumPath<GroupType> type = createEnum("type", GroupType.class);

    public QGroup(String variable) {
        this(Group.class, forVariable(variable), INITS);
    }

    public QGroup(Path<? extends Group> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGroup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGroup(PathMetadata metadata, PathInits inits) {
        this(Group.class, metadata, inits);
    }

    public QGroup(Class<? extends Group> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QGroup(forProperty("parent"), inits.get("parent")) : null;
        this.schedule = inits.isInitialized("schedule") ? new QSchedule(forProperty("schedule")) : null;
    }

}

