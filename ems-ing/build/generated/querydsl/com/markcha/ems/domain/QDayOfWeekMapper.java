package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDayOfWeekMapper is a Querydsl query type for DayOfWeekMapper
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDayOfWeekMapper extends EntityPathBase<DayOfWeekMapper> {

    private static final long serialVersionUID = -960174163L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDayOfWeekMapper dayOfWeekMapper = new QDayOfWeekMapper("dayOfWeekMapper");

    public final QDayOfWeek dayOfWeek;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSchedule schedule;

    public QDayOfWeekMapper(String variable) {
        this(DayOfWeekMapper.class, forVariable(variable), INITS);
    }

    public QDayOfWeekMapper(Path<? extends DayOfWeekMapper> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDayOfWeekMapper(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDayOfWeekMapper(PathMetadata metadata, PathInits inits) {
        this(DayOfWeekMapper.class, metadata, inits);
    }

    public QDayOfWeekMapper(Class<? extends DayOfWeekMapper> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dayOfWeek = inits.isInitialized("dayOfWeek") ? new QDayOfWeek(forProperty("dayOfWeek")) : null;
        this.schedule = inits.isInitialized("schedule") ? new QSchedule(forProperty("schedule")) : null;
    }

}

