package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTagKey is a Querydsl query type for TagKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QTagKey extends BeanPath<TagKey> {

    private static final long serialVersionUID = 871048544L;

    public static final QTagKey tagKey = new QTagKey("tagKey");

    public final StringPath tagname = createString("tagname");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public QTagKey(String variable) {
        super(TagKey.class, forVariable(variable));
    }

    public QTagKey(Path<? extends TagKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagKey(PathMetadata metadata) {
        super(TagKey.class, metadata);
    }

}

