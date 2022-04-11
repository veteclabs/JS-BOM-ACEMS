package com.markcha.ems.controller;

import com.markcha.ems.domain.Voltage;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.device.TemplcateDto;
import com.markcha.ems.dto.group.GroupsSimpleDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.group.GroupRepository;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.DeviceServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    private final DeviceRepository deviceDslRepository;
    private final GroupRepository groupDslRepository;
    private final DeviceServiceImpl deviceService;
    private final DeviceDataRepository deviceDataRepository;

    public DeviceController(
            @Qualifier("deviceDslRepositoryImpl") DeviceRepository deviceDslRepository,
            GroupDslRepositoryImpl groupDslRepository, DeviceServiceImpl deviceService, DeviceDataRepository deviceDataRepository) {
        this.deviceDslRepository = deviceDslRepository;
        this.groupDslRepository = groupDslRepository;
        this.deviceService = deviceService;
        this.deviceDataRepository = deviceDataRepository;
    }

    @GetMapping(value="/etcs")
    public List<TemplcateDto> etc(
    ) {
        return deviceDslRepository.findAllTemplcates("compressor").stream()
                .map(TemplcateDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/device/groups")
    public List<GroupsSimpleDto> group(
    ) {
        return groupDslRepository.findAllByType("group").stream()
                .map(GroupsSimpleDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/device/compressors")
    public List<CompressorSimpleDto> templcate(
    ) {
        return groupDslRepository.findAllByType("compressor").stream()
                .map(CompressorSimpleDto::new)
                .collect(Collectors.toList());
    }



    @PostMapping(value="/etcs")
    public ApiResponseDto etcCreate(
            @RequestBody DeviceInsertDto deviceInsert
    ) {
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

