package com.markcha.ems.controller;

import com.markcha.ems.dto.device.AirCompressorDto;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.GroupDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.CompressorServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CompressorController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final CompressorServiceImpl compressorService;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final GroupDataRepository groupDataRepository;


    @GetMapping(value="/compressors", headers="setting=true")
    public List<CompressorDto> compressors(
    ) {
        return deviceDslRepository.findAllCompressors(AIR_COMPRESSOR)
                .stream()
                .map(CompressorDto::new)
                .collect(toList());
    }

    @GetMapping(value="/compressors")
    public List<AirCompressorDto> compressor(
    ) {
        return deviceDslRepository.findAllCompressorsJoinEquipment(AIR_COMPRESSOR)
                .stream()
                .map(AirCompressorDto::new)
                .collect(toList());
    }
    @GetMapping(value="/compressor/{compressorId}")
    public AirCompressorDto compressor(
            @PathVariable("compressorId") Long compressorId
    ) {
        return new AirCompressorDto(deviceDslRepository.getOneCompressorsJoinEquipment(compressorId, AIR_COMPRESSOR));
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
    @DeleteMapping(value="/compressors")
    public ApiResponseDto delete(
            @RequestBody List<Long> ids
    ) {

        groupDataRepository.deleteAllById(ids);
        return new ApiResponseDto(dbDeleteMsg);
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class CompressorInsertDto {
        private Long id;
        private String name;
        private Long groupId;
        private ScheduleDto schedule;
        private List<String> dayOfWeeks;
        private List<String> weeks;
    }
}
