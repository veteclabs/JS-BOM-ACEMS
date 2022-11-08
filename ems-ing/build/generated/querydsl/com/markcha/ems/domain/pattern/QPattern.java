package com.markcha.ems.domain.pattern;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPattern is a Querydsl query type for Pattern
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPattern extends EntityPathBase<Pattern> {

    private static final long serialVersionUID = 1331725463L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPattern pattern = new QPattern("pattern");

    public final NumberPath<Integer> afterSleep = createNumber("afterSleep", Integer.class);

    public final NumberPath<Integer> beforeSleep = createNumber("beforeSleep", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public final QPatternList patternList;

    public final NumberPath<Double> requireValue = createNumber("requireValue", Double.class);

    public final com.markcha.ems.domain.QTagList tagList;

    public final ComparablePath<Character> variableName = createComparable("variableName", Character.class);

    public QPattern(String variable) {
        this(Pattern.class, forVariable(variable), INITS);
    }

    public QPattern(Path<? extends Pattern> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPattern(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPattern(PathMetadata metadata, PathInits inits) {
        this(Pattern.class, metadata, inits);
    }

    public QPattern(Class<? extends Pattern> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.patternList = inits.isInitialized("patternList") ? new QPatternList(forProperty("patternList"), inits.get("patternList")) : null;
        this.tagList = inits.isInitialized("tagList") ? new com.markcha.ems.domain.QTagList(forProperty("tagList"), inits.get("tagList")) : null;
    }

}

