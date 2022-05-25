package com.markcha.ems.controller;

import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.markcha.ems.service.InsertSampleData;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private final WebaccessApiServiceImpl webaccessApiService;

    @GetMapping(value="/totalValue")
    public Map<String, Object> compressor(@PathVariable("id") Long id) {

        return webaccessApiService.getTagValuesV2(new ArrayList<>(List.of("032_RPM", "031_STATE")));
    }
}
