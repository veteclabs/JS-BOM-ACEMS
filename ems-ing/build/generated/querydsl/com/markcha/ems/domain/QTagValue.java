package com.markcha.ems.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTagValue is a Querydsl query type for TagValue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagValue extends EntityPathBase<TagValue> {

    private static final long serialVersionUID = -430941134L;

    public static final QTagValue tagValue = new QTagValue("tagValue");

    public final StringPath Name = createString("Name");

    public final NumberPath<Integer> Quality = createNumber("Quality", Integer.class);

    public final NumberPath<Double> Value = createNumber("Value", Double.class);

    public QTagValue(String variable) {
        super(TagValue.class, forVariable(variable));
    }

    public QTagValue(Path<? extends TagValue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagValue(PathMetadata metadata) {
        super(TagValue.class, metadata);
    }

}

