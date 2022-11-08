package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLink is a Querydsl query type for Link
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLink extends EntityPathBase<Link> {

    private static final long serialVersionUID = 720228277L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLink link = new QLink("link");

    public final QGroup childs;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QGroup parents;

    public QLink(String variable) {
        this(Link.class, forVariable(variable), INITS);
    }

    public QLink(Path<? extends Link> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLink(PathMetadata metadata, PathInits inits) {
        this(Link.class, metadata, inits);
    }

    public QLink(Class<? extends Link> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.childs = inits.isInitialized("childs") ? new QGroup(forProperty("childs"), inits.get("childs")) : null;
        this.parents = inits.isInitialized("parents") ? new QGroup(forProperty("parents"), inits.get("parents")) : null;
    }

}

