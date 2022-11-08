package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWeekMapper is a Querydsl query type for WeekMapper
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeekMapper extends EntityPathBase<WeekMapper> {

    private static final long serialVersionUID = 637184720L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWeekMapper weekMapper = new QWeekMapper("weekMapper");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<Order, QOrder> orders = this.<Order, QOrder>createSet("orders", Order.class, QOrder.class, PathInits.DIRECT2);

    public final QSchedule schedule;

    public final QWeek week;

    public QWeekMapper(String variable) {
        this(WeekMapper.class, forVariable(variable), INITS);
    }

    public QWeekMapper(Path<? extends WeekMapper> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWeekMapper(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWeekMapper(PathMetadata metadata, PathInits inits) {
        this(WeekMapper.class, metadata, inits);
    }

    public QWeekMapper(Class<? extends WeekMapper> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new QSchedule(forProperty("schedule")) : null;
        this.week = inits.isInitialized("week") ? new QWeek(forProperty("week")) : null;
    }

}

