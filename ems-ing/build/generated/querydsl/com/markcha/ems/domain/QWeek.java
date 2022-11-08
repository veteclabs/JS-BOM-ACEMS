package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWeek is a Querydsl query type for Week
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeek extends EntityPathBase<Week> {

    private static final long serialVersionUID = 720551855L;

    public static final QWeek week = new QWeek("week");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> idx = createNumber("idx", Integer.class);

    public final StringPath name = createString("name");

    public final SetPath<WeekMapper, QWeekMapper> weekMappers = this.<WeekMapper, QWeekMapper>createSet("weekMappers", WeekMapper.class, QWeekMapper.class, PathInits.DIRECT2);

    public QWeek(String variable) {
        super(Week.class, forVariable(variable));
    }

    public QWeek(Path<? extends Week> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeek(PathMetadata metadata) {
        super(Week.class, metadata);
    }

}

