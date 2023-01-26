package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.pattern.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatternQueryDto extends PatternDto {
    private TagListDownStreamQueryDto tagList;
    public PatternQueryDto(Pattern pattern) {
        this.id = pattern.getId();
        this.afterSleep = pattern.getAfterSleep();
        this.beforeSleep = pattern.getBeforeSleep();
        this.requireValue = pattern.getRequireValue();
        this.variableName = pattern.getVariableName();
        this.tagList = new TagListDownStreamQueryDto(pattern.getTagList());
    }
}
