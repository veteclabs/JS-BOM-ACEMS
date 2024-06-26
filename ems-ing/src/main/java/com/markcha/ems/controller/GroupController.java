package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.exception.custom.MethodNotAllowedException;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.service.impl.GroupServiceImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.POWER_METER;
import static com.markcha.ems.domain.QEnergy.energy;
import static com.markcha.ems.domain.QEquipment.equipment;
import static com.markcha.ems.domain.QTag.tag;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

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
    private final GroupDynamicRepositoryImpl groupDynamicRepository;

    @GetMapping(value="/groups", headers = "setting=true")
    public List<GroupDto> showSetting() {
        List<Group> collect = groupDslRepository.findAllJoinSchedule();
        return collect.stream()
                .map((group) -> new GroupDto(group))
                .collect(toList());
    }
    @GetMapping(value="/core")
    public List<GroupQueryDto> core(
            @RequestBody GroupSearchDto groupSearchDto
    ) {
        groupSearchDto.setTagInTypes(QTag.tag.type.in(groupSearchDto.getTagTypes()));
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds(GroupType.GROUP);
        return groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupSearchDto, true).stream()
                .map(t->new GroupQueryDto(t, groupSearchDto.getDetail()))
                .collect(toList());
    }

    @GetMapping(value="/group/{groupId}")
    public GroupDto showOne(
            @PathVariable("groupId") Long groupId
    ) {
        return new GroupDto(groupDslRepository.getOneJoinSchedule(groupId));
    }
    @GetMapping(value="/orphans")
    public Map<String, List<DeviceDto>> orphans() {
        List<Device> devices = new ArrayList<>();
        List<Long> deviceIds = new ArrayList<>();
        devices.addAll(deviceDslRepository.findAllAirOrphs());
        devices.addAll(deviceDslRepository.findAllEtcOrphs());
        List<Tag> tags = groupDslRepository.getTagsByDeviceIds(devices.stream()
                .map(t->t.getId()).collect(toList()));

        Map<String, Object> tagValues = webaccessApiService.getTagValuesV2(tags.stream()
                .map(k -> k.getTagName())
                .collect(toList()));
        tags.forEach(t ->  t.setValue(tagValues.get(t.getTagName())));

        Map<Long, List<Tag>> groupByTag = tags.stream()
                .collect(groupingBy(t -> t.getDevice().getId()));
        devices.forEach(t->t.setTags(new HashSet<>(groupByTag.get(t.getId()))));

        Map<String, List<DeviceDto>> collect = devices.stream()
                .map(t->new DeviceDto(t , true, true))
                .collect(groupingBy(t -> t.getType().getNickname()));
        List<String> typeList = new ArrayList<>();
        typeList.add("power");
        typeList.add("flow");
        typeList.add("pressure");
        typeList.add("temperature");
        typeList.add("airCompressor");
        typeList.forEach(t->{
            if(!collect.keySet().contains(t)) {
                collect.put(t, new ArrayList<>());
            }
        });
        return collect;
    }
    @GetMapping(value="/groups", headers = "setting=false")
    public List<GroupDto> show()  {
        List<String> typeList = new ArrayList<>();
        typeList.add(EquipmentType.POWER_METER.getNickname());
        typeList.add(EquipmentType.FLOW_METER.getNickname());
        typeList.add(EquipmentType.PRESSURE_GAUGE.getNickname());
        typeList.add(EquipmentType.THERMO_METER.getNickname());
        return groupDslRepository.findAllGroupJoinTags().stream()
                .map(g->new GroupDto(g, typeList, true))
                .collect(toList());
    }
    @PutMapping(value="/groups/check/schedule/{groupId}", headers = "setting=true")
    public List<GroupServiceImpl.RemoveDayOfWeekDto> checkSchedule(
            @RequestBody GroupInsertDto groupInsertDto,
            @PathVariable("groupId") Long groupId
    )  {
        groupInsertDto.setId(groupId);
        return groupService.checkSchedule(groupInsertDto);
    }
    @PostMapping(value="/group")
    public ApiResponseDto create(
            @RequestBody GroupInsertDto groupInsertDto
    ) {
        if (groupInsertDto.getSchedule().getIsActive()) {
            throw new MethodNotAllowedException("장비관리에서 압력계에 그룹 연동을 먼저 진행해주세요.\n(해당 그룹에 압력계가 없을 경우 스케줄 제어가 불가능합니다.)");
        }
        groupService.createGruops(groupInsertDto);
        return new ApiResponseDto(dbInsertMsg);
    }
    @PutMapping(value="/group/{groupId}")
    public ApiResponseDto update(
            @RequestBody GroupInsertDto groupInsertDto,
            @PathVariable("groupId") Long groupId
    ) {
        if (groupDslRepository.countPressure(groupId) == 0 && groupInsertDto.getSchedule().getIsActive()) {
            throw new MethodNotAllowedException("장비관리에서 압력계에 그룹 연동을 먼저 진행해주세요.\n(해당 그룹에 압력계가 없을 경우 스케줄 제어가 불가능합니다.)");
        } else if (groupDslRepository.countPressure(groupId) > 1 && groupInsertDto.getSchedule().getIsActive()) {
            throw new MethodNotAllowedException("그룹 스케줄 제어를 위한 압력계를 하나만 연결해주세요.\n(그룹에 연결된 압력계가 2개 이상일 경우 스케줄 제어가 불가능합니다.)");
        }
        groupInsertDto.setId(groupId);
        groupService.updateCompressor(groupInsertDto);
        return new ApiResponseDto(dbUpdateMsg);
    }
    @PutMapping(value="/groups")
    public ApiResponseDto updateAll(
            @RequestBody List<GroupDto> groupInsertDtos
    ) {
        groupInsertDtos.forEach(t->{
            if(t.getDevices().get("pressure").size() > 1) {
                throw new MethodNotAllowedException("그룹 안에 하나의 압력계만 연결해주세요.\n(각 그룹에 연결된 압력계가 2개 이상일 경우 그룹 수정이 불가능합니다.");
            }
        });
        groupInsertDtos.forEach(t->t.setDeviceList(t));
        groupService.updateGroups(groupInsertDtos);
        return new ApiResponseDto(dbUpdateMsg);
    }


    @DeleteMapping(value="/groups")
    public ApiResponseDto deleteAll(
            @RequestBody List<Long> ids
    ) {
        groupService.deleteGroups(ids);
        return new ApiResponseDto(dbDeleteMsg);
    }
    @Data
    @NoArgsConstructor
    public static class GroupInsertDto {
        private Long id;
        private String name;
        private ScheduleDto schedule;
        private TagDto tags;
    }
    @Data
    public static class GroupSearchDto {
        private Integer level;
        private Long energyId;
        private Boolean detail;
        private EquipmentType equipmentType;
        private List<EquipmentType> equipmentTypes;
        private List<String> tagTypes;
        private String tagType;
        private Boolean isUsage;
        private Long deviceIdT;



        @JsonIgnore
        private BooleanExpression equipmentEqType;
        @JsonIgnore
        private BooleanExpression energyEqId;
        @JsonIgnore
        private BooleanExpression tagEqType;
        @JsonIgnore
        private BooleanExpression tagEqIsUsage;
        @JsonIgnore
        private BooleanExpression deviceEqId;

        @JsonIgnore
        private BooleanExpression tagInTypes;
        public GroupSearchDto() {
            if(!isNull(equipmentType)) this.equipmentEqType = equipment.type.eq(equipmentType);
            if(!isNull(energyId)) this.energyEqId = energy.id.eq(energyId);
            if(!isNull(tagType)) this.tagEqType = tag.type.eq(tagType);
            if(!isNull(tagTypes)) this.tagInTypes = tag.type.in(tagTypes);
        }
        public GroupSearchDto(Integer level, Long energyId, Boolean detail, EquipmentType equipmentType, String tagType, Boolean isUsage, Long deviceId) {
            this.level = level;
            this.energyId = energyId;
            this.detail = detail;
            this.equipmentType = equipmentType;
            this.tagType = tagType;
            if(!isNull(equipmentType)) this.equipmentEqType = equipment.type.eq(equipmentType);
            if(!isNull(energyId)) this.energyEqId = energy.id.eq(energyId);
            if(!isNull(tagType)) this.tagEqType = tag.type.eq(tagType);
            if(!isNull(tagTypes)) this.tagInTypes = tag.type.in(tagTypes);
        }
        public void setIsUsage(Boolean isUsage) {
            this.isUsage = isUsage;
            this.tagEqIsUsage = tag.isUsage.eq(isUsage);
        }
        public void setTagType(String tagType) {
            this.tagType = tagType;
            this.tagEqType = tag.type.eq(tagType);
        }
        public void setTagTypes(List<String> tagTypes) {
            this.tagTypes = tagTypes;
            this.tagInTypes = tag.type.in(tagTypes);
        }
        public void setDevoceIdT2(String deviceIdT) {
            if(!isNull(deviceIdT) && !deviceIdT.equals("AU")) this.deviceIdT = Long.parseLong(deviceIdT);
            if(!deviceIdT.equals("AU")) this.deviceEqId = QDevice.device.id.eq(this.deviceIdT);
        }
        public void setEquipmentType(EquipmentType equipmentType) {
            this.equipmentType = equipmentType;
            this.equipmentEqType = equipment.type.eq(equipmentType);
        }
        public void convertTagTypeName() {
            switch(this.tagType) {
                case "PF":
                    setTagType("PWR_PF");
                    break;
                case "KWH":
                    setTagType("PWR_KWh");
                    break;
                case "FLOW":
                    setTagType("AIR_Con");
                    break;
            }
        }
    }
}
