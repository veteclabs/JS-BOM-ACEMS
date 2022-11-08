package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = -356164398L;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final SetPath<DayOfWeekMapper, QDayOfWeekMapper> dayOfWeekMappers = this.<DayOfWeekMapper, QDayOfWeekMapper>createSet("dayOfWeekMappers", DayOfWeekMapper.class, QDayOfWeekMapper.class, PathInits.DIRECT2);

    public final SetPath<Group, QGroup> groups = this.<Group, QGroup>createSet("groups", Group.class, QGroup.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> interval = createNumber("interval", Integer.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final BooleanPath isGroup = createBoolean("isGroup");

    public final NumberPath<Double> max = createNumber("max", Double.class);

    public final NumberPath<Double> min = createNumber("min", Double.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> stopTime = createTime("stopTime", java.time.LocalTime.class);

    public final StringPath type = createString("type");

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public final SetPath<WeekMapper, QWeekMapper> weekMappers = this.<WeekMapper, QWeekMapper>createSet("weekMappers", WeekMapper.class, QWeekMapper.class, PathInits.DIRECT2);

    public QSchedule(String variable) {
        super(Schedule.class, forVariable(variable));
    }

    public QSchedule(Path<? extends Schedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedule(PathMetadata metadata) {
        super(Schedule.class, metadata);
    }

}

