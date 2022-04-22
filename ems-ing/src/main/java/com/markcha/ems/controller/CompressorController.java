package com.markcha.ems.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.controller.analysis.DataAnalysisController;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.dto.device.AirCompressorDto;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.tag.response.TagResultDto;
import com.markcha.ems.mapper.alarm.AlarmMapDto;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.GroupDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.service.impl.CompressorServiceImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
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
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final WebaccessApiServiceImpl webaccessApiService;

    @GetMapping(value="/compressors", headers="setting=true")
    public List<CompressorDto> compressors(
    ) {
        List<CompressorDto> collect = deviceDslRepository.findAllCompressors(AIR_COMPRESSOR).stream()
                .map(CompressorDto::new)
                .collect(toList());

        collect.forEach(t->{
            if(!isNull(t.getSchedule())) {
                ScheduleDto scheduleDto = t.getSchedule();
                Map<String, Double> tagValues = webaccessApiService.getTagValuesV2(
                        t.getTags().stream()
                                .map(g -> g.getTagName())
                                .collect(toList()));
                t.getTags().forEach(k->{
                    k.setValue(tagValues.get(k.getTagName()));
                });
                for (TagDto tag : t.getTags()) {
                    if(tag.getType().equals("PRESSURE_MIN")) scheduleDto.setMin(tag.getValue());
                    if(tag.getType().equals("PRESSURE_MAX")) scheduleDto.setMax(tag.getValue());
                }
                scheduleDto.setMinMax();

            }

        });
        return collect;
    }
    @GetMapping(value="/compressors")
    public List<AirCompressorDto> compressor(
    ) {
        List<AirCompressorDto> allJoinAlarm = compressorService.findAllJoinAlarm();
        allJoinAlarm.forEach(t->{
            if(!isNull(t.getSchedule())) {
                ScheduleDto scheduleDto = t.getSchedule();
                Map<String, Double> tagValues = webaccessApiService.getTagValuesV2(
                        t.getTags().stream()
                                .map(g -> g.getTagName())
                                .collect(toList()));
                t.getTags().forEach(k->{
                    k.setValue(tagValues.get(k.getTagName()));
                });
                for (TagDto tag : t.getTags()) {
                    if(tag.getType().equals("PRESSURE_MIN")) scheduleDto.setMin(tag.getValue());
                    if(tag.getType().equals("PRESSURE_MAX")) scheduleDto.setMax(tag.getValue());
                }
                scheduleDto.setMinMax();
            }
        });
        return allJoinAlarm;
    }
    @GetMapping(value="/compressor/{compressorId}")
    public AirCompressorDto compressor(
            @PathVariable("compressorId") Long compressorId
    ) {
        AirCompressorDto oneJoinAlarm = compressorService.getOneJoinAlarm(compressorId);

        if(!isNull(oneJoinAlarm.getSchedule())) {
            ScheduleDto scheduleDto = oneJoinAlarm.getSchedule();
            Map<String, Double> tagValues = webaccessApiService.getTagValuesV2(
                    oneJoinAlarm.getTags().stream()
                            .map(g -> g.getTagName())
                            .collect(toList()));
            oneJoinAlarm.getTags().forEach(k->{
                k.setValue(tagValues.get(k.getTagName()));
            });
            for (TagDto tag : oneJoinAlarm.getTags()) {
                if(tag.getType().equals("PRESSURE_MIN")) scheduleDto.setMin(tag.getValue());
                if(tag.getType().equals("PRESSURE_MAX")) scheduleDto.setMax(tag.getValue());
            }
            scheduleDto.setMinMax();
        }
        return oneJoinAlarm;
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
        compressorService.deleteAllById(ids);
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
