package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.QEnergy;
import com.markcha.ems.domain.QEquipment;
import com.markcha.ems.domain.QTag;
import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.GroupServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.QEnergy.energy;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QTag.tag;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;
    private final GroupServiceImpl groupService;
    private final GroupDslRepositoryImpl groupDslRepository;
    @GetMapping(value="/groups", headers = "setting=true")
    public List<GroupDto> showSetting() {
        List<GroupDto> collect = groupDslRepository.findAllJoinSchedule().stream()
                .map((group) -> new GroupDto(group))
                .collect(toList());
        return collect;
    }
    @GetMapping(value="/group/{groupId}")
    public GroupDto showOne(
            @PathVariable("groupId") Long groupId
    ) {
        return new GroupDto(groupDslRepository.getOneJoinSchedule(groupId));
    }
    @GetMapping(value="/groups", headers = "setting=false")
    public List<GroupDto> show() throws JsonProcessingException {
        try {
        return groupDslRepository.findAllGroupJoinTags().stream()
                .map((group)->new GroupDto(group, true))
                .collect(toList());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    @PostMapping(value="/group")
    public ApiResponseDto create(
            @RequestBody GroupInsertDto groupInsertDto
    ) {
        groupService.createGruops(groupInsertDto);
        return new ApiResponseDto(dbInsertMsg);
    }
    @PutMapping(value="/group/{groupId}")
    public ApiResponseDto update(
            @RequestBody GroupInsertDto groupInsertDto,
            @PathVariable("groupId") Long groupId
    ) {
        groupInsertDto.setId(groupId);
        groupInsertDto.getSchedule().setId(groupId);
        groupService.updateCompressor(groupInsertDto);
        return new ApiResponseDto(dbUpdateMsg);
    }
    @PutMapping(value="/groups")
    public ApiResponseDto updateAll(
            @RequestBody List<GroupDto> groupInsertDtos
    ) {
        groupInsertDtos.forEach(t-> System.out.println(t.getName()));
        System.out.println(groupInsertDtos.get(0).getAirCompressors().get(0).getName());
        return new ApiResponseDto(dbUpdateMsg);
    }
    @Data
    @NoArgsConstructor
    public static class GroupInsertDto {
        private Long id;
        private String name;
        private ScheduleDto schedule;
    }
    @Data
    public static class GroupSearchDto {
        private Integer level;
        private Long energyId;
        private Boolean detail;
        private EquipmentType equipmentType;
        private String tagType;
        private Boolean isUsage;

        @JsonIgnore
        private BooleanExpression equipmentEqType;
        @JsonIgnore
        private BooleanExpression energyEqId;
        @JsonIgnore
        private BooleanExpression tagEqType;
        @JsonIgnore
        private BooleanExpression tagEqIsUsage;
        public GroupSearchDto(Integer level, Long energyId, Boolean detail, EquipmentType equipmentType, String tagType, Boolean isUsage) {
            this.level = level;
            this.energyId = energyId;
            this.detail = detail;
            this.equipmentType = equipmentType;
            this.tagType = tagType;
            if(!isNull(equipmentType)) this.equipmentEqType = equipment.type.eq(equipmentType);
            if(!isNull(energyId)) this.energyEqId = energy.id.eq(energyId);
            if(!isNull(tagType)) this.tagEqType = tag.type.eq(tagType);
            if(!isNull(isUsage)) this.tagEqIsUsage = tag.isUsage.eq(isUsage);
        }
        public void setIsUsage(Boolean isUsage) {
            this.isUsage = isUsage;
            this.tagEqIsUsage = tag.isUsage.eq(isUsage);
        }
        public void setTagType(String tagType) {
            this.tagType = tagType;
            this.tagEqType = tag.type.eq(tagType);
        }
    }
}
