package com.markcha.ems.dto.group;

import com.markcha.ems.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupsSimpleDto {
    private Long id;
    private String name;

    public GroupsSimpleDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
    }
}
