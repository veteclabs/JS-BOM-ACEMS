package com.markcha.ems.controller.equipment.dto;

import com.markcha.ems.domain.*;
import com.markcha.ems.domain.pattern.PatternList;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDto {
    private Long id;
    private String name;
    private EquipmentType type;
    private String maker;
    private String model;
    private String description;
    private List<TagListDto> tagList = new ArrayList<>();
    public EquipmentDto(Equipment equipment) {
        this.id = equipment.getId();
        this.name = equipment.getName();
        this.type = equipment.getType();
        this.maker = equipment.getMaker();
        this.model = equipment.getModel();
        this.description = equipment.getDescription();
        this.tagList = equipment.getTagLists().stream()
                .map(t-> new TagListDto(t))
                .collect(Collectors.toList());

    }
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagListDto {
        private String tagDescription;
        private Boolean isAlarm;
        private Boolean isTrend;
        private Boolean isUsage;
        private Integer loggingTime;
        private String nickname;
        private String type;
        private String unit;
        public TagListDto(TagList tagList) {
            this.tagDescription = tagList.getTagDescription();
            this.isAlarm = tagList.getIsAlarm();
            this.isTrend = tagList.getIsTrend();
            this.isUsage = tagList.getIsUsage();
            this.loggingTime = tagList.getLoggingTime();
            this.nickname = tagList.getNickname();
            this.type = tagList.getType();
            this.unit = tagList.getUnit();
        }
    }
}
