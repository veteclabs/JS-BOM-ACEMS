package com.markcha.ems.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.markcha.ems.domain.TagValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultDto {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Value")
    private Double Value;
    @JsonProperty("Quality")
    private Integer Quality;
    public ResultDto(TagValue tagValue) {
        this.Name = tagValue.getName();
        this.Value = tagValue.getValue();
        this.Quality = tagValue.getQuality();
    }
}
