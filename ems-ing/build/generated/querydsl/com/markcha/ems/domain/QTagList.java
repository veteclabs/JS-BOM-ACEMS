package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTagList is a Querydsl query type for TagList
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagList extends EntityPathBase<TagList> {

    private static final long serialVersionUID = 1232734653L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTagList tagList = new QTagList("tagList");

    public final QEquipment equipment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAlarm = createBoolean("isAlarm");

    public final BooleanPath isTrend = createBoolean("isTrend");

    public final BooleanPath isUsage = createBoolean("isUsage");

    public final NumberPath<Integer> loggingTime = createNumber("loggingTime", Integer.class);

    public final NumberPath<Double> max = createNumber("max", Double.class);

    public final NumberPath<Double> min = createNumber("min", Double.class);

    public final StringPath nickname = createString("nickname");

    public final SetPath<com.markcha.ems.domain.pattern.PatternList, com.markcha.ems.domain.pattern.QPatternList> patternList = this.<com.markcha.ems.domain.pattern.PatternList, com.markcha.ems.domain.pattern.QPatternList>createSet("patternList", com.markcha.ems.domain.pattern.PatternList.class, com.markcha.ems.domain.pattern.QPatternList.class, PathInits.DIRECT2);

    public final BooleanPath showAble = createBoolean("showAble");

    public final StringPath tagDescription = createString("tagDescription");

    public final StringPath tagName = createString("tagName");

    public final SetPath<Tag, QTag> tags = this.<Tag, QTag>createSet("tags", Tag.class, QTag.class, PathInits.DIRECT2);

    public final SetPath<TagSetMapper, QTagSetMapper> tagSetMappers = this.<TagSetMapper, QTagSetMapper>createSet("tagSetMappers", TagSetMapper.class, QTagSetMapper.class, PathInits.DIRECT2);

    public final StringPath testType = createString("testType");

    public final StringPath type = createString("type");

    public final StringPath unit = createString("unit");

    public final BooleanPath unitConversion = createBoolean("unitConversion");

    public final NumberPath<Double> value = createNumber("value", Double.class);

    public QTagList(String variable) {
        this(TagList.class, forVariable(variable), INITS);
    }

    public QTagList(Path<? extends TagList> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTagList(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTagList(PathMetadata metadata, PathInits inits) {
        this(TagList.class, metadata, inits);
    }

    public QTagList(Class<? extends TagList> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.equipment = inits.isInitialized("equipment") ? new QEquipment(forProperty("equipment")) : null;
    }

}

