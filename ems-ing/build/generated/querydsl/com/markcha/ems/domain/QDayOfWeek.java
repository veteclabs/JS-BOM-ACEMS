package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDayOfWeek is a Querydsl query type for DayOfWeek
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDayOfWeek extends EntityPathBase<DayOfWeek> {

    private static final long serialVersionUID = 1273964108L;

    public static final QDayOfWeek dayOfWeek = new QDayOfWeek("dayOfWeek");

    public final SetPath<DayOfWeekMapper, QDayOfWeekMapper> dayOfWeekMappers = this.<DayOfWeekMapper, QDayOfWeekMapper>createSet("dayOfWeekMappers", DayOfWeekMapper.class, QDayOfWeekMapper.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> idx = createNumber("idx", Integer.class);

    public final StringPath name = createString("name");

    public QDayOfWeek(String variable) {
        super(DayOfWeek.class, forVariable(variable));
    }

    public QDayOfWeek(Path<? extends DayOfWeek> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDayOfWeek(PathMetadata metadata) {
        super(DayOfWeek.class, metadata);
    }

}

