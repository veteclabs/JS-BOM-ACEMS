package com.markcha.ems.controller;

import com.markcha.ems.dto.device.CompressorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;


//    @GetMapping(value="/schedules")
//    public List<CompressorDto> schedules(
//    ) {
//        return deviceDslRepository.findAllCompressors(AIR_COMPRESSOR)
//                .stream()
//                .map(CompressorDto::new)
//                .collect(toList());
//    }
//
//    @GetMapping(value="/schedule/{scheduleId")
//    public List<CompressorDto> schedule(
//            @PathVariable("compressorId") Long compressorId
//    ) {
//        return deviceDslRepository.findAllCompressors(AIR_COMPRESSOR)
//                .stream()
//                .map(CompressorDto::new)
//                .collect(toList());
//    }
//
//    @GetMapping(value="/schedules")
//    public List<CompressorDto> schedules(
//    ) {
//        return deviceDslRepository.findAllCompressors(AIR_COMPRESSOR)
//                .stream()
//                .map(CompressorDto::new)
//                .collect(toList());
//    }
}
