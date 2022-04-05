package com.markcha.ems.repository.group.impl;

import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.QDevice;
import com.markcha.ems.domain.QGroup;
import com.markcha.ems.repository.group.GroupRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QGroup.group;

@Repository
public class GroupDslRepositoryImpl implements GroupRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public GroupDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    public List<Group> findAllByType(String type) {
        return queryFactory.select(group)
                .from(group)
                .where(
                        group.type.eq(type)
                ).fetch();
    }
    public Group getOneById(Long id) {
        return queryFactory.select(group)
                .from(group)
                .where(group.id.eq(id))
                .fetchOne();
    }

    @Override
    public Group getOneByDeviceId(Long id) {
        return queryFactory.select(group)
                .from(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .where(device.id.eq(id))
                .fetchOne();
    }
}
