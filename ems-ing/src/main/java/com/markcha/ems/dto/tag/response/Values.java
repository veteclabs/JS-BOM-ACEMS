package com.markcha.ems.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Values {
    @JsonProperty("resultDtos")
    private List<TagResultDto> resultDtos = new ArrayList<TagResultDto>();
}
