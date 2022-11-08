package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTagSet is a Querydsl query type for TagSet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagSet extends EntityPathBase<TagSet> {

    private static final long serialVersionUID = 871056227L;

    public static final QTagSet tagSet = new QTagSet("tagSet");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final SetPath<TagSetMapper, QTagSetMapper> tagSetMappers = this.<TagSetMapper, QTagSetMapper>createSet("tagSetMappers", TagSetMapper.class, QTagSetMapper.class, PathInits.DIRECT2);

    public QTagSet(String variable) {
        super(TagSet.class, forVariable(variable));
    }

    public QTagSet(Path<? extends TagSet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagSet(PathMetadata metadata) {
        super(TagSet.class, metadata);
    }

}

