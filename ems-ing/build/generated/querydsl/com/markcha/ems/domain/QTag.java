package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTag is a Querydsl query type for Tag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTag extends EntityPathBase<Tag> {

    private static final long serialVersionUID = -808043393L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTag tag = new QTag("tag");

    public final ListPath<Alarm, QAlarm> alarms = this.<Alarm, QAlarm>createList("alarms", Alarm.class, QAlarm.class, PathInits.DIRECT2);

    public final QDevice device;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAlarm = createBoolean("isAlarm");

    public final BooleanPath isTrend = createBoolean("isTrend");

    public final BooleanPath isUsage = createBoolean("isUsage");

    public final NumberPath<Integer> loggingTime = createNumber("loggingTime", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final BooleanPath showAble = createBoolean("showAble");

    public final StringPath tagDescription = createString("tagDescription");

    public final QTagList tagList;

    public final StringPath tagName = createString("tagName");

    public final StringPath type = createString("type");

    public final StringPath unit = createString("unit");

    public final BooleanPath unitConversion = createBoolean("unitConversion");

    public QTag(String variable) {
        this(Tag.class, forVariable(variable), INITS);
    }

    public QTag(Path<? extends Tag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTag(PathMetadata metadata, PathInits inits) {
        this(Tag.class, metadata, inits);
    }

    public QTag(Class<? extends Tag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.device = inits.isInitialized("device") ? new QDevice(forProperty("device"), inits.get("device")) : null;
        this.tagList = inits.isInitialized("tagList") ? new QTagList(forProperty("tagList"), inits.get("tagList")) : null;
    }

}

