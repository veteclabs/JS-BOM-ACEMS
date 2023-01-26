package com.markcha.ems.controller;

import com.markcha.ems.dto.alarm.AlarmDto;
import com.markcha.ems.dto.alarm.TripDto;
import com.markcha.ems.repository.TripDataRepository;
import com.markcha.ems.repository.alarm.AlarmDslRepositoryImpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

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
    private final AlarmDslRepositoryImpl alarmDslRepository;

    @GetMapping(value="/trip")
    public Map<Integer, String> etc(
    ) {
        return tripDataRepository.findAll().stream()
                .map(TripDto::new)
                .collect(toMap(TripDto::getCode, t->t.getMessage()));
    }
    @GetMapping(value="/alarms/{groupId}")
    public List<AlarmDto> alarm(
            @PathVariable("groupId") Long groupId
    ) {
        return alarmDslRepository.findAllByGroupId(groupId).stream()
                .map(AlarmDto::new)
                .collect(toList());
    }
}
