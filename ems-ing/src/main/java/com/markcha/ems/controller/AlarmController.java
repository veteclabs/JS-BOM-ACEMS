package com.markcha.ems.controller;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.dto.alarm.TripDto;
import com.markcha.ems.repository.TripDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlarmController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;

    private final TripDataRepository tripDataRepository;

    @GetMapping(value="/trip")
    public List<TripDto> etc(
    ) {
        return tripDataRepository.findAll().stream()
                .map(TripDto::new)
                .collect(toList());
    }
}
