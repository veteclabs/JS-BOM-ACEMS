package com.markcha.ems.controller.equipment.dto;

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
public class StateTagSetDto {
    private Long id;
    private String nickname;
    private String description;
    private List<StateEquipmentDto> equipments = new ArrayList<>();

    public StateTagSetDto(TagSet tagSet) {
        id = tagSet.getId();
        nickname = tagSet.getNickname();
        description = tagSet.getDescription();
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StateEquipmentDto {
        private Long id;
        private String name;
        private EquipmentType type;
        private String maker;
        private String model;
        private String description;
        private List<StateTagListDto> tagList = new ArrayList<>();

        public StateEquipmentDto(Equipment equipment) {
            this.id = equipment.getId();
            this.name = equipment.getName();
            this.type = equipment.getType();
            this.maker = equipment.getMaker();
            this.model = equipment.getModel();
            this.description = equipment.getDescription();
            this.tagList = equipment.getTagLists().stream()
                    .map(t -> new StateTagListDto(t))
                    .collect(Collectors.toList());

        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StateTagListDto {
        private Long id;
        private String keywork;
        private String description;
        private Long tagListId = null;
        private String TagListName = null;
        private boolean isActive = false;

        public StateTagListDto(TagList tagList) {
//            this.id = tagList.getId();
//            this.type = tagList.getType();
//            this.unit = tagList.getUnit();
        }
        public static List<StateTagListDto> defaultList() {

            return new ArrayList<>() {
                {
                    StateTagListDto.builder()
                            .keywork("COMP_Power")
                            .description("시작/정지")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_SystemPre")
                            .description("시스템 압력")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_AutoStop")
                            .description("자동 정지")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_StartPre")
                            .description("시작 압력")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_StopPre")
                            .description("정지 압력")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_ActTripCode")
                            .description("트립 코드 번호")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_ActWarCode")
                            .description("경고 코드 번호")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_Trip")
                            .description("트립 상태")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_Warning")
                            .description("경고 상태")
                            .build();
                    StateTagListDto.builder()
                            .keywork("COMP_LoadFactor")
                            .description("부하율")
                            .build();

                }
            };
        }
    }
}
