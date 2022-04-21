package com.markcha.ems.controller;

import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.GroupType;
import com.markcha.ems.domain.Voltage;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.device.TemplcateDto;
import com.markcha.ems.dto.group.GroupsSimpleDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.exception.custom.MethodNotAllowedException;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.DeviceServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api")
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

    public DeviceController(
            DeviceDslRepositoryImpl deviceDslRepository,
            GroupDslRepositoryImpl groupDslRepository, DeviceServiceImpl deviceService, DeviceDataRepository deviceDataRepository) {
        this.deviceDslRepository = deviceDslRepository;
        this.groupDslRepository = groupDslRepository;
        this.deviceService = deviceService;
        this.deviceDataRepository = deviceDataRepository;
    }

    @GetMapping(value="/etcs",headers = "setting=true")
    public List<TemplcateDto> etc(
    ) throws Exception {
        return deviceDslRepository.findAllTemplcates(AIR_COMPRESSOR).stream()
                .map(TemplcateDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/etcs")
    public List<TemplcateDto> etc2(
    ){
        System.out.println(22);
        return deviceDslRepository.findAllTemplcates(AIR_COMPRESSOR).stream()
                .map(t->new TemplcateDto(t,true))
                .collect(Collectors.toList());
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

