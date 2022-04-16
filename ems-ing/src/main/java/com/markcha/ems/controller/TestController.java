package com.markcha.ems.controller;

import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.AirCompressorDto;
import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.markcha.ems.service.InsertSampleData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final EquipmentDslRepositoryImpl equipmentDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final DayOfWeekDataRepository dayOfWeekDataRepository;
    private final GroupDataRepository groupDataRepository;
    private final WeekDataRepository weekDataRepository;
    private final WeekMapperDataRepository weekMapperDataRepository;
    private final DayOfWeekMapperDataRepository dayOfWeekMapperDataRepository;
    private final ScheduleDataRepository scheduleDataRepository;
    private final ScheduleDslRepositoryImpl scheduleDslRepository;
    private final DayOfWeekMapperDslRepositoryImpl dayOfWeekMapperDslRepository;
    private final WeekMapperDslRepositoryImpl weekMapperDslRepository;
    private final InsertSampleData insertSampleData;
    private final AlarmMapper alarmMapper;

    @GetMapping(value="/device/{id}")
    public List<GroupDto> compressor(@PathVariable("id") Long id) {

        Group group = groupDslRepository.getOneById(id);
        Schedule schedule = group.getSchedule();
        schedule.setGroups(null);
        group.setSchedule(null);
        groupDataRepository.save(group);
        Schedule a = scheduleDslRepository.a(5L);
        group.setSchedule(a);
        a.getGroups().add(group);

        groupDataRepository.save(group);
        return null;

    }
}
