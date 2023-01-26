package com.markcha.ems.controller.equipment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.TagList;
import com.markcha.ems.domain.TagSet;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagSetDto {
    private Long id;
    private String nickname;
    private String description;
    private List<EquipmentSetDto> equipments = new ArrayList<>();

    public TagSetDto(TagSet tagSet) {
        id = tagSet.getId();
        nickname = tagSet.getNickname();
        description = tagSet.getDescription();
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EquipmentSetDto {
        private Long id;
        private String name;
        private EquipmentType type;
        private String maker;
        private String model;
        private String description;
        private List<TagListSetDto> tagList = new ArrayList<>();

        public EquipmentSetDto(Equipment equipment) {
            this.id = equipment.getId();
            this.name = equipment.getName();
            this.type = equipment.getType();
            this.maker = equipment.getMaker();
            this.model = equipment.getModel();
            this.description = equipment.getDescription();
            this.tagList = equipment.getTagLists().stream()
                    .map(t -> new TagListSetDto(t))
                    .collect(Collectors.toList());

        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagListSetDto {
        private Long id;
        private String tagDescription;
        private Boolean isAlarm;
        private Boolean isTrend;
        private Boolean isUsage;
        private Integer loggingTime;
        private String nickname;
        private String type;
        private String unit;
        private boolean isActive = false;

        public TagListSetDto(TagList tagList) {
            this.id = tagList.getId();
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
