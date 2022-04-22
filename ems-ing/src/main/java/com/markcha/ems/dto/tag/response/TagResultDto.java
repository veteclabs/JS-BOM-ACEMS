package com.markcha.ems.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.markcha.ems.domain.TagValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TagResultDto {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Value")
    private Double Value;
    @JsonProperty("Quality")
    private Integer Quality;
    public TagResultDto(TagValue tagValue) {
        this.Name = tagValue.getName();
        this.Value = tagValue.getValue();
        this.Quality = tagValue.getQuality();
    }
}
