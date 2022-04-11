package com.markcha.ems.repository.group.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.markcha.ems.domain.QDayOfWeek.dayOfWeek;
import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QOrder.order1;
import static com.markcha.ems.domain.QSchedule.schedule;
import static com.markcha.ems.domain.QTag.tag;
import static com.markcha.ems.domain.QWeek.week;
import static com.markcha.ems.domain.QWeekMapper.weekMapper;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Repository
public class GroupDslRepositoryImpl implements GroupRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;
    private final WebaccessApiServiceImpl webaccessApiService;

    public GroupDslRepositoryImpl(EntityManager entityManager, WebaccessApiServiceImpl webaccessApiService) {
        this.entityManager = entityManager;
        this.query = new JPAQueryFactory(entityManager);
        this.webaccessApiService = webaccessApiService;
    }
    @Override
    public List<Group> findAllByType(String type) {
        return query.select(group)
                .from(group)
                .where(
                        group.type.eq(type)
                ).fetch();
    }
    @Override
    public Group getOneById(Long id) {
        return query.select(group)
                .from(group)
                .where(group.id.eq(id))
                .fetchOne();
    }

    @Override
    public Group getOneByDeviceId(Long id) {
        return query.select(group)
                .from(group)
                .leftJoin(group.deviceSet, device).fetchJoin()
                .where(device.id.eq(id))
                .fetchOne();
    }

    private List<WeekMapper> getWeekMapper(List<Long> scheduleIds) {
        return query.select(weekMapper)
                .from(weekMapper).distinct()
                .leftJoin(weekMapper.orders, order1).fetchJoin()
                .leftJoin(order1.group, group).fetchJoin()
                .leftJoin(group.deviceSet, device)
                .leftJoin(device.equipment, equipment)
                .leftJoin(weekMapper.week, week).fetchJoin()
                .where(
                         weekMapper.schedule.id.in(scheduleIds)
                        ,equipment.type.eq("compressor").or(
                                order1.isNull()
                        )
                )
                .fetch();
    }
    private List<Group> getRootGroups() {
        return query.select(group)
                .from(group).distinct()
                .leftJoin(group.schedule, schedule).fetchJoin()
                .leftJoin(schedule.weekMappers, weekMapper).fetchJoin()
                .leftJoin(weekMapper.week, week).fetchJoin()
                .leftJoin(schedule.dayOfWeekMappers, dayOfWeekMapper)
                .leftJoin(dayOfWeekMapper.dayOfWeek, dayOfWeek)
                .where(
                        group.type.eq("group")
                )
                .fetch();
    }
    private List<Group> getGroups() {
        QGroup childGroup = new QGroup("cg");
        QDevice childDevice = new QDevice("cd");
        QTag childTag = new QTag("ct");
        return query.selectFrom(group).distinct()
                .leftJoin(group.children, childGroup).fetchJoin()
                .leftJoin(group.deviceSet, device)
                .leftJoin(device.tags, tag)
                .leftJoin(childGroup.deviceSet, childDevice)
                .leftJoin(childDevice.tags, childTag)
                .where(group.type.eq("group"))
                .fetch();
    }
    private List<Tag> getTagsByDeviceIds(List<Long> deviceIds) {
        return query.selectFrom(tag).distinct()
                .join(tag.device, device).fetchJoin()
                .where(
                         tag.showAble.eq(true)
                        ,device.id.in(deviceIds))
                .fetch();
    }
    public List<Group> findAllGroupJoinTags() throws JsonProcessingException {
        List<Group> groups = getGroups();
        List<Long> deviceIds = new ArrayList<>();
        groups.forEach(g->{
            g.getChildren().forEach(c->c.getDeviceSet().forEach(d->deviceIds.add(d.getId())));
            g.getDeviceSet().forEach(d->deviceIds.add(d.getId()));
        });
        List<Tag> tags = getTagsByDeviceIds(deviceIds);
        Map<Long, List<Tag>> groupByTag = tags.stream()
                .collect(groupingBy(t -> t.getDevice().getId()));
        groups.forEach(g->{
            g.getDeviceSet().forEach(d->{
                if (!isNull(groupByTag.get(d.getId()))) {
                    List<TagDto> tagList = groupByTag.get(d.getId()).stream()
                            .map(TagDto::new)
                            .collect(toList());
                    List<String> tagNames = tagList.stream()
                            .map(k -> k.getTagName())
                            .collect(toList());
                    try {
                        List<JsonNode> tagValues = webaccessApiService.getTagValues(tagNames);
                        tagList.forEach(a -> {
                            List<JsonNode> tags2 = tagValues.stream()
                                    .filter(l -> a.getTagName().equals(l.get("Name").toString().replace("\"", "")))
                                    .collect(toList());
                            if(!isNull(tags2) && tags2.size() == 1) {
                                a.setValue(tags2.get(0).get("Value").doubleValue());
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (!isNull(d.getTagList())) d.setTagList(tagList);
                }
            });
            g.getChildren().forEach(c-> c.getDeviceSet().forEach(d->{
                if (!isNull(groupByTag.get(d.getId()))) {
                    List<TagDto> tagList = groupByTag.get(d.getId()).stream()
                            .map(TagDto::new)
                            .collect(toList());
                    List<String> tagNames = tagList.stream()
                            .map(k -> k.getTagName())
                            .collect(toList());
                    try {
                        List<JsonNode> tagValues = webaccessApiService.getTagValues(tagNames);
                        tagList.forEach(a -> {
                            List<JsonNode> tags2 = tagValues.stream()
                                    .filter(l -> a.getTagName().equals(l.get("Name").toString().replace("\"", "")))
                                    .collect(toList());
                            if(!isNull(tags2) && tags2.size() == 1) {
                                a.setValue(tags2.get(0).get("Value").doubleValue());
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (!isNull(d.getTagList())) d.setTagList(tagList);
                }
                }));
        });
        return groups;
    }
    private List<Group> getChildGroups(List<Long> parentGroupIds) {
        QGroup parentGroup = new QGroup("cg");
        return query.selectFrom(group)
                .leftJoin(group.parent, parentGroup).fetchJoin()
                .leftJoin(group.deviceSet, device).fetchJoin()
                .leftJoin(device.equipment, equipment).fetchJoin()
                .where(
                         parentGroup.id.in(parentGroupIds)
                        ,equipment.type.eq("compressor")
                )
                .fetch();
    }
    @Override
    public List<Group> findAllChildGroupsById(Long id) {
        QGroup childGroup = new QGroup("cg");
        return query.selectFrom(group)
                .leftJoin(group.children, childGroup).fetchJoin()
                .fetch();
    }
    @Override
    public List<Group> findAllByIds(List<Long> ids) {
        return query.selectFrom(group).where(group.id.in(ids)).fetch();
    }
    @Override
    public List<Group> findAllJoinSchedule() {
        List<Group> rootGroup = getRootGroups();
        List<Long> scheduleIds = rootGroup.stream()
                .map((t) -> t.getSchedule().getId())
                .collect(toList());
        List<Long> parentGroupIds = rootGroup.stream()
                .map((t) -> t.getId())
                .collect(toList());
        List<WeekMapper> weekMappers = getWeekMapper(scheduleIds);

        List<Group> childGroups = getChildGroups(parentGroupIds);
        Map<Long, List<Group>> groupByParentId = childGroups.stream()
                .collect(groupingBy(t -> t.getParent().getId()));
        weekMappers.forEach(t->
            t.getSchedule().getGroups().forEach(k->{
                List<Group> groups = groupByParentId.get(k.getId());
                t.setStandByGroups(groups);
            }));

        Map<Long, List<WeekMapper>> grouppingWeekMapper = weekMappers.stream()
                .collect(groupingBy(weekMapper -> weekMapper.getSchedule().getId()));
        rootGroup.forEach(t->t.getSchedule().setWeeks(grouppingWeekMapper.get(t.getSchedule().getId())));

        return rootGroup;

    }
}
