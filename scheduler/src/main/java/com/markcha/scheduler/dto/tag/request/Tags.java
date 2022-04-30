package com.markcha.scheduler.dto.tag.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tags {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Value")
    private Double Value;
}
