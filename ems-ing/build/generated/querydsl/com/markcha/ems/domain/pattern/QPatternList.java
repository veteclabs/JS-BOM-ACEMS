package com.markcha.ems.domain.pattern;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPatternList is a Querydsl query type for PatternList
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPatternList extends EntityPathBase<PatternList> {

    private static final long serialVersionUID = -336427563L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPatternList patternList = new QPatternList("patternList");

    public final StringPath description = createString("description");

    public final StringPath formula = createString("formula");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<IOType> IOType = createEnum("IOType", IOType.class);

    public final StringPath name = createString("name");

    public final SetPath<Pattern, QPattern> patterns = this.<Pattern, QPattern>createSet("patterns", Pattern.class, QPattern.class, PathInits.DIRECT2);

    public final NumberPath<Double> remoteMaxValue = createNumber("remoteMaxValue", Double.class);

    public final NumberPath<Double> remoteMinValue = createNumber("remoteMinValue", Double.class);

    public final com.markcha.ems.domain.QTagList tagList;

    public QPatternList(String variable) {
        this(PatternList.class, forVariable(variable), INITS);
    }

    public QPatternList(Path<? extends PatternList> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPatternList(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPatternList(PathMetadata metadata, PathInits inits) {
        this(PatternList.class, metadata, inits);
    }

    public QPatternList(Class<? extends PatternList> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tagList = inits.isInitialized("tagList") ? new com.markcha.ems.domain.QTagList(forProperty("tagList"), inits.get("tagList")) : null;
    }

}

