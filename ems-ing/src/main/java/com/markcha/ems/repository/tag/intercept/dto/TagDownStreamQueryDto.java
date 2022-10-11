package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagDownStreamQueryDto extends TagDto {
    public TagDownStreamQueryDto(Tag tag) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.value = tag.getValue();
    }
}
