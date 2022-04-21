package com.markcha.ems.repository.group.impl;

import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.repository.group.GroupRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

import static com.markcha.ems.domain.QEnergy.energy;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QTag.tag;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Repository
public class GroupDynamicRepositoryImpl extends QuerydslRepositorySupport {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * param domainClass must not be {@literal null}.
     */
    public GroupDynamicRepositoryImpl(EntityManager em) {
        super(Group.class);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    
    public List<Long> getLevelIds(Integer level) {
        return queryFactory.select(
                Projections.constructor(
                        Long.class,
                        group.id
                ))
                .from(group)
                .where(group.level.eq(level))
                .fetch();
    }

    
    public List<Long> getTypeIds(GroupType type) {
        return queryFactory.select(
                Projections.constructor(
                        Long.class,
                        group.id
                ))
                .from(group)
                .where(group.type.eq(type))
                .fetch();
    }

    private List<Group> getLocations(BooleanExpression in) {
        return queryFactory.select(group)
                .from(group)
                .where(
                        in
                )
                .fetch();
    }

    private List<Device> getDevices(List<Long> ids, GroupSearchDto locationSearchDto) {
        return queryFactory.select(device)
                .from(device)
                .leftJoin(device.energy, energy).fetchJoin()
                .leftJoin(device.tags, tag).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         device.group.id.in(ids)
                        ,locationSearchDto.getEnergyEqId()
                        ,locationSearchDto.getEquipmentEqType()
                        ,locationSearchDto.getTagEqType()
                        ,locationSearchDto.getTagEqIsUsage()
                        ,locationSearchDto.getDeviceEqId()
                        ,locationSearchDto.getTagInTypes()
                ).fetch();
    }

    private List<Group> getLocationNode(BooleanExpression in, GroupSearchDto locationSearchDto) {

        List<Group> locations = getLocations(in);
        List<Long> locationIds = locations.stream()
                .map(location -> location.getId())
                .collect(toList());
        List<Device> equipmentMgmts = getDevices(locationIds, locationSearchDto);
        if (!equipmentMgmts.isEmpty()) {
            Map<Long, List<Device>> equipmentMgmtGroup = equipmentMgmts.stream()
                    .collect(groupingBy(equipmentMgmt -> equipmentMgmt.getGroup().getId()));

            locations.forEach(location -> {
                        List<Device> devices = equipmentMgmtGroup.get(location.getId());
                        if (!Objects.isNull(devices)) {
                            location.setDevices( new HashSet<>(devices));
                        }
                    }
            );
        }
        return locations;
    }

    
    public List<Group> getAnalysisLocations(List<Long> locaionIds, GroupSearchDto locationSearchDto, Boolean deep) {
        List<Group> parentLocations = getLocationNode(
                deep? group.id.in(locaionIds):group.parent.id.in(locaionIds),
                locationSearchDto);
        if (parentLocations.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> parentIds = parentLocations.stream()
                .map(parent -> parent.getId())
                .collect(toList());
        List<Group> childLocations = getAnalysisLocations(parentIds, locationSearchDto, false);

        if (childLocations.isEmpty()) {
            parentLocations.removeIf(parentLocation -> parentLocation.getDevices().isEmpty());
            return parentLocations;
        }
        Map<Long, List<Group>> childLocationGroup = childLocations.stream()
                .collect(groupingBy(cheildLocation -> cheildLocation.getParent().getId()));
        parentLocations.forEach(parent ->parent.setChilds(childLocationGroup.get(parent.getId())));
        return parentLocations;
    }
}
