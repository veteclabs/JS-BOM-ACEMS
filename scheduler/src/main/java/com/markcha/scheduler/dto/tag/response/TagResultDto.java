package com.markcha.scheduler.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.markcha.scheduler.domain.TagValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TagResultDto {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Value")
    private Object Value;
    @JsonProperty("Quality")
    private Integer Quality;
    public TagResultDto(TagValue tagValue) {
        this.Name = tagValue.getName();
        this.Value = tagValue.getValue();
        this.Quality = tagValue.getQuality();
    }
}
