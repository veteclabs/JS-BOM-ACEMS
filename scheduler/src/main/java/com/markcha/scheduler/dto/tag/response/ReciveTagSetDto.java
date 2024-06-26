package com.markcha.scheduler.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReciveTagSetDto {
    @JsonProperty("Result")
    private Result Result;
    @JsonProperty("Values")
    private List<TagResultDto> Values = new ArrayList<>();
}
