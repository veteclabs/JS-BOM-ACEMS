package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.TagList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TagListDownStreamQueryDto extends TagListDto {
    private TagDownStreamQueryDto tag;
    public TagListDownStreamQueryDto(TagList tagList) {
        this.id = tagList.getId();
        this.tagDescription = tagList.getTagDescription();
        this.unit = tagList.getUnit();
        this.unitConversion = tagList.getUnitConversion();
        this.isUsage = tagList.getIsUsage();
        this.isTrend = tagList.getIsTrend();
        this.loggingTime = tagList.getLoggingTime();
        this.nickname = tagList.getNickname();
        this.isAlarm = tagList.getIsAlarm();
        this.showAble = tagList.getShowAble();
        this.type = tagList.getType();
        this.min = tagList.getMin();
        this.max = tagList.getMax();
        this.testType = tagList.getTestType();
        if(!isNull(tagList.getTags()) && tagList.getTags().size() > 0)
            this.tag = new TagDownStreamQueryDto(new ArrayList<>(tagList.getTags()).get(0));
    }
}
