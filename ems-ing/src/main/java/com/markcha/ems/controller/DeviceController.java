package com.markcha.ems.controller;

import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.device.DeviceConDto;
import com.markcha.ems.dto.device.TemplcateDto;
import com.markcha.ems.dto.group.GroupsSimpleDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.exception.custom.MethodNotAllowedException;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.DeviceServiceImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeviceController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;

    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final DeviceServiceImpl deviceService;
    private final DeviceDataRepository deviceDataRepository;
    private final WebaccessApiServiceImpl webaccessApiService;



    @GetMapping(value="/etcs",headers = "setting=true")
    public List<TemplcateDto> etc(
    ) throws Exception {
        return deviceDslRepository.findAllTemplcates(AIR_COMPRESSOR).stream()
                .map(TemplcateDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/etcs")
    public Map<String, List<DeviceConDto>> etc2(
    ){
        List<Device> allTemplcates = deviceDslRepository.findAllTemplcates(AIR_COMPRESSOR);
        List<String> tagNames = new ArrayList<>();
        allTemplcates.forEach(t->t.getTags().forEach(k->tagNames.add(k.getTagName())));

        Map<String, Object> tagValuesV2 = webaccessApiService.getTagValuesV2(tagNames);
        allTemplcates.forEach(t->t.getTags().forEach(tag->tag.setValue(tagValuesV2.get(tag.getTagName()))));
        return allTemplcates.stream()
                .map(DeviceConDto::new)
                .collect(Collectors.groupingBy(t->t.getEquipmentType().getNickname()));
    }
    @GetMapping(value="/device/groups")
    public List<GroupsSimpleDto> group(
    ) {
        return groupDslRepository.findAllByType(GroupType.GROUP).stream()
                .map(GroupsSimpleDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/device/compressors")
    public List<CompressorSimpleDto> templcate(
    ) {
        return groupDslRepository.findAllByType(GroupType.OBJECT).stream()
                .map(CompressorSimpleDto::new)
                .collect(Collectors.toList());
    }



    @PostMapping(value="/etcs")
    public ApiResponseDto etcCreate(
            @RequestBody DeviceInsertDto deviceInsert
    ) {
        long l = deviceDslRepository.countingGroupHavePressureDevice(deviceInsert.getGroupId());
        if (deviceDslRepository.countingCompressorHavePowerDevice() == 0 && isNull(deviceInsert.getGroupId())) {
            throw new MethodNotAllowedException("전체 전력계 중 하나 이상의 컴프레셔 그룹이 필요합니다.");
        }
        deviceService.createDevice(deviceInsert);
        return new ApiResponseDto(dbInsertMsg);
    }
    @DeleteMapping(value="/etcs")
    public ApiResponseDto etcDeletes(
            @RequestBody List<Long> ids
    ) {
        deviceDataRepository.deleteAllById(ids);
        return new ApiResponseDto(dbDeleteMsg);
    }
    @PutMapping(value="/etc/{deviceId}")
    public ApiResponseDto etcDeletes(
            @RequestBody DeviceInsertDto deviceInsert,
            @PathVariable("deviceId") Long deviceId
    ) {

        Boolean haveGroup = !isNull(deviceDataRepository.getOne(deviceId).getGroup())? true: false;
        if (deviceDslRepository.countingCompressorHavePowerDevice() == 1
                && isNull(deviceInsert.getGroupId())
                && haveGroup
        ) {
            throw new MethodNotAllowedException("전체 전력계 중 하나 이상의 컴프레셔 그룹이 필요합니다.");
        }

        deviceInsert.setId(deviceId);
        deviceService.updateDevice(deviceInsert);
        return new ApiResponseDto(dbUpdateMsg);
    }

    @Data
    @NoArgsConstructor
    public static class DeviceInsertDto {
        private Long id;
        private String name;
        private Long groupId;
        private String type;
        private String model;
        private Double ct;
        private Double pt;
        private Voltage voltage;
    }
}

