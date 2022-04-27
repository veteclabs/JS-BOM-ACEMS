package com.markcha.ems.repository.tag;

import com.markcha.ems.domain.QDevice;
import com.markcha.ems.domain.QGroup;
import com.markcha.ems.domain.QTag;
import com.markcha.ems.domain.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QTag.tag;

@Repository
public class TagDslRepositoryIml {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;

    public TagDslRepositoryIml(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
    }

    public Tag findAllByGroupId(Long id){

        return query.selectFrom(tag)
                .leftJoin(tag.device, device).fetchJoin()
                .leftJoin(device.group, group).fetchJoin()
                .where(
                         group.id.eq(id)
                        ,tag.type.eq("COMP_Power")
                ).fetchOne();

    }
}
