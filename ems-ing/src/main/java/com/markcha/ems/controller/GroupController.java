package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.QEnergy;
import com.markcha.ems.domain.QEquipment;
import com.markcha.ems.domain.QTag;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.GroupServiceImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.QEnergy.energy;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QTag.tag;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
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
    private final WebaccessApiServiceImpl webaccessApiService;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final DeviceDslRepositoryImpl deviceDslRepository;
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
    @GetMapping(value="/orphans")
    public Map<String, List<DeviceDto>> orphans() {
        List<DeviceDto> devices = new ArrayList<>();
        List<Long> deviceIds = new ArrayList<>();

        devices.addAll(deviceDslRepository.findAllAirOrphs().stream()
                .map(t->{
                    deviceIds.add(t.getId());
                    return new DeviceDto(t,true);
                })
                .collect(toList()));
        devices.addAll(deviceDslRepository.findAllEtcOrphs().stream()
                .map(t-> {
                    deviceIds.add(t.getId());
                    return new DeviceDto(t);
                })
                .collect(toList()));

        List<TagDto> tags = groupDslRepository.getTagsByDeviceIds(deviceIds).stream()
                .map(TagDto::new)
                .collect(toList());

        List<String> tagNames = tags.stream()
                .map(k -> k.getTagName())
                .collect(toList());
        try {
            List<JsonNode> tagValues = webaccessApiService.getTagValues(tagNames);
            tags.forEach(a -> {
                List<JsonNode> tags2 = tagValues.stream()
                        .filter(l -> a.getTagName().equals(l.get("Name").toString().replace("\"", "")))
                        .collect(toList());
                if(!isNull(tags2) && tags2.size() == 1) {
                    a.setValue(tags2.get(0).get("Value").doubleValue());
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Map<Long, List<TagDto>> groupByTag = tags.stream()
                .collect(groupingBy(t -> t.getDeviceId()));
        devices.forEach(t->t.setTags(groupByTag.get(t.getDeviceId())));

        Map<String, List<DeviceDto>> collect = devices.stream()
                .collect(groupingBy(t -> t.getType().getNickname()));
        List<String> typeList = new ArrayList<>();
        typeList.add("power");
        typeList.add("flow");
        typeList.add("pressure");
        typeList.add("temperature");
        typeList.forEach(t->{
            if(!collect.keySet().contains(t)) {
                collect.put(t, new ArrayList<>());
            }
        });
        return collect;
    }
    @GetMapping(value="/groups", headers = "setting=false")
    public List<GroupDto> show() throws JsonProcessingException {

        try {
            List<String> typeList = new ArrayList<>();
            typeList.add("power");
            typeList.add("flow");
            typeList.add("pressure");
            typeList.add("temperature");
            return groupDslRepository.findAllGroupJoinTags().stream()
                    .map((group)->new GroupDto(group, typeList, true))
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
        groupInsertDtos.stream()
                .forEach(t -> {
                    List<DeviceDto> devices = new ArrayList<>();
                    t.getDevices().forEach((key, deviceDtos)-> devices.addAll(deviceDtos));
                    t.setDeviceList(devices);
                });
        groupService.updateGroups(groupInsertDtos);
        return new ApiResponseDto(dbUpdateMsg);
    }
    @DeleteMapping(value="/groups")
    public ApiResponseDto deleteAll(
            @RequestBody List<Long> ids
    ) {

        groupService.deleteGroups(ids);
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
