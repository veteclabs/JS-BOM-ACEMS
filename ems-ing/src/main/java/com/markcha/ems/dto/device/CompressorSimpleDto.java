package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompressorSimpleDto {
    private Long id;
    private String name;
    private Integer order;
    @JsonIgnore
    private Group saveGroup;
    public CompressorSimpleDto(Group group) {
        if(!isNull(group)) {
            this.name = group.getName();
            this.id = group.getId();
            this.saveGroup = group;
        }
    }
    public CompressorSimpleDto(Group group, Integer order) {
        if(!isNull(group)) {
            this.name = group.getName();
            this.id = group.getId();
            this.saveGroup = group;
        }
        this.order = order;
    }
}
