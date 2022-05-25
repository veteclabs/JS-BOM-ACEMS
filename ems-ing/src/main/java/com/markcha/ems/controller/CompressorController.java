package com.markcha.ems.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.controller.analysis.DataAnalysisController;
import com.markcha.ems.domain.*;
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
import com.markcha.ems.repository.tag.TagDslRepositoryIml;
import com.markcha.ems.service.impl.CompressorServiceImpl;
import com.markcha.ems.service.impl.GroupServiceImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static com.markcha.ems.domain.QGroup.group;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

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
    private final TagDslRepositoryIml tagDslRepositoryIml;
    private final GroupServiceImpl groupService;

    @GetMapping(value="/compressors", headers="setting=true")
    public List<CompressorDto> compressors(
    ) {
        List<CompressorDto> collect = deviceDslRepository.findAllCompressors(AIR_COMPRESSOR, null).stream()
                .map(CompressorDto::new)
                .collect(toList());

        collect.forEach(t->{
            if(!isNull(t.getSchedule())) {
                ScheduleDto scheduleDto = t.getSchedule();
                if(!isNull(t.getTags().get("COMP_StartPre"))) scheduleDto.setMin(t.getTags().get("COMP_StartPre").getValue());
                if(!isNull(t.getTags().get("COMP_StopPre"))) scheduleDto.setMax(t.getTags().get("COMP_StopPre").getValue());
                scheduleDto.setMinMax();
            }
        });
        return collect;
    }

    @GetMapping(value="/compressors")
    public List<AirCompressorDto> compressor(
    ) {
        List<AirCompressorDto> collect = compressorService.findAllJoinAlarm(null).stream()
                .map(AirCompressorDto::new)
                .collect(toList());
        collect.forEach(t->{
            if(!isNull(t.getSchedule())) {
                ScheduleDto scheduleDto = t.getSchedule();
                scheduleDto.setMin(t.getState().get("COMP_StartPre").getValue());
                scheduleDto.setMax(t.getState().get("COMP_StopPre").getValue());
                scheduleDto.setMinMax();
            }
        });
        return collect;
    }
    @GetMapping(value="/compressor/{compressorId}")
    public AirCompressorDto compressor(
            @PathVariable("compressorId") Long compressorId
    ) {
        System.out.println(23);
        List<AirCompressorDto> collect = compressorService.findAllJoinAlarm(group.id.eq(compressorId)).stream()
                .map(AirCompressorDto::new)
                .collect(toList());
        collect.forEach(t->{
            if(!isNull(t.getSchedule())) {
                ScheduleDto scheduleDto = t.getSchedule();
                scheduleDto.setMin(t.getState().get("COMP_StartPre").getValue());
                scheduleDto.setMax(t.getState().get("COMP_StopPre").getValue());
                scheduleDto.setMinMax();
            }
        });
        if(collect.size() == 1) {
            return collect.get(0);
        }
        return null;
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
        return new ApiResponseDto(dbUpdateMsg);
    }
    @PutMapping(value="/compressor/{groupId}/power/{powerCode}")
    public ApiResponseDto create2(
            @PathVariable("groupId") Long groupId,
            @PathVariable("powerCode") Integer powerCode
    ) throws InterruptedException {
        Device device = tagDslRepositoryIml.findAllByGroupId(groupId);
        Map<String, TagDto> tags = device.getTags().stream()
                .map(TagDto::new)
                .collect(toMap(TagDto::getType, t->t));
        compressorService.controllPowerAsync(device.getGroup().getSchedule().getInterval(), tags, powerCode);
        Group group = groupDslRepository.findAllBroGroupByBroId(groupId);
        if(!isNull(group)) groupService.unActiveSchedule(group);
        return new ApiResponseDto("제어명령이 성공적으로 실행되었습니다. 프로그램 통신 상 제어 명령 반응까지 몇 분이 소요될 수 도 있습니다.");
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
