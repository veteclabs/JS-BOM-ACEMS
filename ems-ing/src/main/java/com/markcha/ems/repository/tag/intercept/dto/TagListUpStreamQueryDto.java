package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.TagList;
import lombok.Data;

@Data
public class TagListUpStreamQueryDto extends TagListDto {
    private PatternListQueryDto patternList;
    public TagListUpStreamQueryDto(TagList tagList) {
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
        this.patternList = new PatternListQueryDto(tagList.getPatternList().iterator().next());
    }
}
