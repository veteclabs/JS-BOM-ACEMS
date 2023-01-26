package com.markcha.ems.dto.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @JsonProperty("Ret")
    private Integer Ret;
    @JsonProperty("Total")
    private Integer Total;
}
