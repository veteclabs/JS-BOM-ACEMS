package com.markcha.ems.repository.tag.intercept.impl;

import com.markcha.ems.domain.*;
import com.markcha.ems.domain.pattern.IOType;
import com.markcha.ems.domain.pattern.PatternList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.markcha.ems.domain.QDevice.device;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QTagList.tagList;
import static com.markcha.ems.domain.QTag.tag;
import static com.markcha.ems.domain.pattern.QPattern.pattern;
import static com.markcha.ems.domain.pattern.QPatternList.patternList;

@Repository
public class TagInterceptRepositoryImpl {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public TagInterceptRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Tag> findAllPatternTags(List<String> tagNames) {
        return query.selectFrom(tag)
                .join(tag.tagList, tagList)
                .join(tagList.patternList, patternList)
                .where(tag.tagName.in(tagNames), patternList.isNotNull()).fetch();
    }
    public List<Tag> findAllByTagNames(List<String> tagNames, IOType ioType) {
        QTag anotherTags = new QTag("anotherTags");
        QTagList anotherTagLists = new QTagList("anotherTagLists");
        QDevice parentDevice = new QDevice("parentDevice");
        QGroup parentGroup = new QGroup("parentGroup");

        return query.selectFrom(tag).distinct()
                .join(tag.device, device).fetchJoin()
                .join(device.group, group).fetchJoin()
                .join(tag.tagList, tagList).fetchJoin()
                .join(tagList.patternList, patternList).fetchJoin()
                .join(patternList.patterns, pattern).fetchJoin()
                .join(pattern.tagList, anotherTagLists).fetchJoin()
                .join(anotherTagLists.tags, anotherTags).fetchJoin()
                .join(anotherTags.device, parentDevice).fetchJoin()
                .join(parentDevice.group, parentGroup).fetchJoin()
                .where(
                         tag.tagName.in(tagNames)
                        ,parentGroup.eq(group)
                        ,patternList.IOType.eq(ioType)
                ).fetch();
    }
    public List<Tag> findAllInputPattern(List<String> tagNames) {
        QTag anotherTags = new QTag("anotherTags");
        QTagList anotherTagLists = new QTagList("anotherTagLists");
        QGroup parentGroup = new QGroup("parentGroup");
        QDevice parentDevice = new QDevice("parentDevice");
        return query.selectFrom(tag).distinct()
                .join(tag.device, device).fetchJoin()
                .join(tag.tagList, tagList).fetchJoin()
                .join(tagList.patternList, patternList).fetchJoin()
                .join(patternList.patterns, pattern).fetchJoin()
                .join(pattern.tagList, anotherTagLists).fetchJoin()
                .join(anotherTagLists.tags, anotherTags).fetchJoin()
                .join(anotherTags.device, parentDevice).fetchJoin()
                .where(
                        tag.tagName.in(tagNames)
                        ,patternList.IOType.eq(IOType.INPUT)
                ).fetch();
    }
    public PatternList getInputPattern(List<String> tagNames, Double remoteValue) {
        QTag anotherTags = new QTag("anotherTags");
        QTagList anotherTagLists = new QTagList("anotherTagLists");
        QGroup parentGroup = new QGroup("parentGroup");
        QDevice parentDevice = new QDevice("parentDevice");
        return query.selectFrom(patternList).distinct()
                .join(patternList.tagList, tagList).fetchJoin()
                .join(tagList.tags, tag).fetchJoin()
                .join(tag.device, device).fetchJoin()
                .join(patternList.patterns, pattern).fetchJoin()
                .join(pattern.tagList, anotherTagLists).fetchJoin()
                .join(anotherTagLists.tags, anotherTags).fetchJoin()
                .join(anotherTags.device, parentDevice).fetchJoin()
                .where(
                        tag.tagName.in(tagNames)
                        ,patternList.IOType.eq(IOType.INPUT)
                        ,parentDevice.id.eq(device.id)
                        ,patternList.remoteMinValue.goe(remoteValue)
                        ,patternList.remoteMaxValue.loe(remoteValue)
                ).fetchOne();
    }
}

