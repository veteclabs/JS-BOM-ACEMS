package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagUpStreamQueryDto extends TagDto {
    private TagListUpStreamQueryDto tagList;
    public TagUpStreamQueryDto(Tag tag) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.value = tag.getValue();
        this.tagList = new TagListUpStreamQueryDto(tag.getTagList());
    }
}