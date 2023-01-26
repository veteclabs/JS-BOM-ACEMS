package com.markcha.scheduler.dto.tag.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendTagSetDto {

    @JsonProperty("Tags")
    private List<Tags> Tags = new ArrayList<>();
}
