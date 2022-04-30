package com.markcha.scheduler.repository.tag;

import com.markcha.scheduler.domain.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.markcha.scheduler.domain.QDevice.device;
import static com.markcha.scheduler.domain.QGroup.group;
import static com.markcha.scheduler.domain.QTag.tag;

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
                        ,tag.type.in(new ArrayList<>(List.of("COMP_Power", "COMP_Local")))
                ).fetchOne();

    }
    public Tag getOneById(Long id) {
        return query.selectFrom(tag)
                .where(tag.id.eq(id))
                .fetchOne();
    }
}
