package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlarm is a Querydsl query type for Alarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlarm extends EntityPathBase<Alarm> {

    private static final long serialVersionUID = 842158582L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarm alarm = new QAlarm("alarm");

    public final BooleanPath checkIn = createBoolean("checkIn");

    public final DatePath<java.time.LocalDate> eventDate = createDate("eventDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> kwhValue = createNumber("kwhValue", Double.class);

    public final StringPath message = createString("message");

    public final TimePath<java.time.LocalTime> occurrenceTime = createTime("occurrenceTime", java.time.LocalTime.class);

    public final NumberPath<Double> prssValue = createNumber("prssValue", Double.class);

    public final DatePath<java.time.LocalDate> recoverDate = createDate("recoverDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> recoverTime = createTime("recoverTime", java.time.LocalTime.class);

    public final QTag tag;

    public final NumberPath<Double> tempValue = createNumber("tempValue", Double.class);

    public final QTrip trip;

    public final StringPath type = createString("type");

    public QAlarm(String variable) {
        this(Alarm.class, forVariable(variable), INITS);
    }

    public QAlarm(Path<? extends Alarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlarm(PathMetadata metadata, PathInits inits) {
        this(Alarm.class, metadata, inits);
    }

    public QAlarm(Class<? extends Alarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag"), inits.get("tag")) : null;
        this.trip = inits.isInitialized("trip") ? new QTrip(forProperty("trip"), inits.get("trip")) : null;
    }

}

