package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTagSetMapper is a Querydsl query type for TagSetMapper
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagSetMapper extends EntityPathBase<TagSetMapper> {

    private static final long serialVersionUID = -1759078524L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTagSetMapper tagSetMapper = new QTagSetMapper("tagSetMapper");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTagList tagList;

    public final QTagSet tagSet;

    public QTagSetMapper(String variable) {
        this(TagSetMapper.class, forVariable(variable), INITS);
    }

    public QTagSetMapper(Path<? extends TagSetMapper> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTagSetMapper(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTagSetMapper(PathMetadata metadata, PathInits inits) {
        this(TagSetMapper.class, metadata, inits);
    }

    public QTagSetMapper(Class<? extends TagSetMapper> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tagList = inits.isInitialized("tagList") ? new QTagList(forProperty("tagList"), inits.get("tagList")) : null;
        this.tagSet = inits.isInitialized("tagSet") ? new QTagSet(forProperty("tagSet")) : null;
    }

}

