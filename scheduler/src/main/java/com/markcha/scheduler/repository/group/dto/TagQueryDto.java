package com.markcha.scheduler.repository.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.scheduler.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagQueryDto {
    private Long id;
    private String tagName;
    private String tagDescription;
    private Boolean isUsage;
    private String type;
    public TagQueryDto(Tag tag) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.tagDescription = tag.getTagDescription();
        this.isUsage = tag.getIsUsage();
        this.type = tag.getType();
    }
}
