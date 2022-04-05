package com.markcha.ems.controller;

import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.service.DeviceService;
import com.markcha.ems.service.impl.CompressorServiceImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CompressorController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;
    private final DeviceRepository deviceDslRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final DeviceService compressorService;

    public CompressorController(
            DeviceDslRepositoryImpl deviceDslRepository,
            DeviceDataRepository deviceDataRepository,
            CompressorServiceImpl compressorService) {
        this.deviceDslRepository = deviceDslRepository;
        this.deviceDataRepository = deviceDataRepository;
        this.compressorService = compressorService;
    }

    @GetMapping(value="/compressors")
    public List<CompressorDto> compressors(
    ) {
        return deviceDslRepository.findAllCompressors("compressor")
                .stream()
                .map(CompressorDto::new)
                .collect(toList());
    }
    @PostMapping(value="/compressor")
    public ApiResponseDto create(
            @RequestBody CompressorInsertDto compressorInsertDto
        ) {
        compressorService.createCompressor(compressorInsertDto);
        return new ApiResponseDto(dbInsertMsg);
    }

    @PutMapping(value="/compressor/{compressorId}")
    public ApiResponseDto create(
            @RequestBody CompressorInsertDto compressorInsertDto,
            @PathVariable("compressorId") Long compressorId
    ) {
        compressorInsertDto.setId(compressorId);
        compressorService.updateCompressor(compressorInsertDto);
        return new ApiResponseDto(dbInsertMsg);
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class CompressorInsertDto {
        private Long id;
        private String name;
        private Long groupId;
        private ScheduleDto schedule;
    }
}
