package com.markcha.ems.repository.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Group;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupQueryDto {
    private Long id;

    private Integer level;

    private String name;
    private String purpose;
    private List<String> tagNames = new ArrayList<>();

    private List<DeviceQueryDto> equipmentMgmt;
    private List<GroupQueryDto> childs;


    public GroupQueryDto(Group location, Boolean isDetail) {
        this.id = location.getId();
        this.level = location.getLevel();
        this.name = location.getName();
        this.purpose = location.getPurpose();

        if(!location.getDevices().isEmpty()) {
            this.equipmentMgmt = location.getDevices().stream()
                    .map(device -> {
                        device.getTags().forEach(tag -> this.tagNames.add(tag.getTagName()));
                        return new DeviceQueryDto(device);
                    })
                    .collect(Collectors.toList());
        } else {
            this.equipmentMgmt = null;
        }

        if(!Objects.isNull(location.getChilds()) && !location.getChilds().isEmpty()) {
            this.childs = location.getChilds().stream()
                    .map(child -> new GroupQueryDto(child, isDetail))
                    .collect(Collectors.toList());
            if (this.tagNames.isEmpty()) {
                this.childs.forEach(child -> {
                    if (!isDetail) this.tagNames.addAll(child.getTagNames());
                });
            }
        } else {
            this.childs = null;
        }
        if (!isDetail) {
            this.childs = null;
            this.equipmentMgmt = null;
        } else {
            this.tagNames = null;
        }

    }
    public GroupQueryDto(Group location) {
        this.id = location.getId();
        this.level = location.getLevel();
        this.name = location.getName();
        this.purpose = location.getPurpose();

        if(!location.getDevices().isEmpty()) {
            this.equipmentMgmt = location.getDevices().stream()
                    .map(device -> {
                        device.getTags().forEach(tag -> this.tagNames.add(tag.getTagName()));
                        return new DeviceQueryDto(device);
                    })
                    .collect(Collectors.toList());
        } else {
            this.equipmentMgmt = null;
        }

        if(!Objects.isNull(location.getChilds()) && !location.getChilds().isEmpty()) {
            this.childs = location.getChilds().stream()
                    .map(GroupQueryDto::new)
                    .collect(Collectors.toList());
            if (this.tagNames.isEmpty()) {
                this.childs.forEach(child -> this.tagNames.addAll(child.getTagNames()));
            }
        } else {
            this.childs = null;
        }


    }
}
