package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController.CompressorInsertDto;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.markcha.ems.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompressorServiceImpl implements DeviceService {
    private final DeviceRepository deviceDslRepository;
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
    @Override
    public Boolean createCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";

        // 스케줄 생성 및 그룹과 연동
        // 스케줄 만 생성
        Schedule newSchedule = new Schedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(true);
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        newSchedule.setMax(scheduleDto.getMax());
        newSchedule.setMin(scheduleDto.getMin());
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());

        // 요일 관계 생성
        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        List<DayOfWeekMapper> newDayOfWeekMappers = new ArrayList<>();
        for (DayOfWeek dayOfWeek: dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newDayOfWeekMappers.add(dayOfWeekMapper);
        }
        // 요일 끝

        // 주차 관계 생성
        List<WeekMapper> newWeekMappers = new ArrayList<>();
        if (!isNull(scheduleDto.getWeeks())){
            List<Long> weekIds = scheduleDto.getWeeks().stream()
                    .map(WeekDto::getId)
                    .collect(toList());
            List<Week> weeks = weekDataRepository.findAllByIdIn(weekIds);

            for (Week week: weeks) {
                WeekMapper weekMapper = new WeekMapper();
                weekMapper.setWeek(week);
                weekMapper.setSchedule(newSchedule);
                newWeekMappers.add(weekMapper);
            }
        }

        newSchedule.setWeekMappers(new HashSet<>(newWeekMappers));
        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
        scheduleDataRepository.save(newSchedule);

        // 그룹 생성 및 부모 그룹 세팅
        Group newGroup = new Group();
        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());
        newGroup.setParent(parentGroup);
        newGroup.setName(compressorInsertDto.getName());
        newGroup.setType(typeName);
        newGroup.setSchedule(newSchedule);
        newGroup.setLevel(2);
        groupDataRepository.save(newGroup);

        // 디바이스 생성 및 그룹과 연
        Device newDevice = new Device();
        Equipment selectedEquipoment = equipmentDslRepository.getOneByType(typeName);
        newDevice.setName(compressorInsertDto.getName());
        newDevice.setEquipment(selectedEquipoment);
        newDevice.setGroup(newGroup);

        selectedEquipoment.getType();
        deviceDataRepository.save(newDevice);
        return true;
    }
    private List<Tag> createTags(Long deviceId, String type) {
        List<Tag> tags = new ArrayList<>();
        String deviceUnit = String.format("%03d", deviceId) + "_";

        switch(type) {
            case "comrpressor":
                Tag rpm = new Tag();
                rpm.setIsAlarm(false);
                rpm.setIsTrend(true);
                rpm.setIsUsage(false);
                rpm.setLoggingTime(300);
                rpm.setNickname("rpm");
                rpm.setShowAble(true);
                rpm.setTagDescription("가동률");
                rpm.setUnit("%");
                rpm.setTagName(deviceUnit + "RPM");
                rpm.setUnitConversion(null);
                tags.add(rpm);

                Tag state = new Tag();
                state.setIsAlarm(false);
                state.setIsTrend(true);
                state.setIsUsage(false);
                state.setLoggingTime(300);
                state.setNickname("state");
                state.setShowAble(true);
                state.setTagDescription("상태");
                state.setUnit(null);
                state.setTagName(deviceUnit + "STATE");
                state.setUnitConversion(null);
                tags.add(state);

                Tag bar = new Tag();
                bar.setIsAlarm(false);
                bar.setIsTrend(true);
                bar.setIsUsage(false);
                bar.setLoggingTime(300);
                bar.setNickname("bar");
                bar.setShowAble(true);
                bar.setTagDescription("압력");
                bar.setUnit(null);
                bar.setTagName(deviceUnit + "BAR");
                bar.setUnitConversion(null);
                tags.add(bar);

                Tag airTemp = new Tag();
                airTemp.setIsAlarm(false);
                airTemp.setIsTrend(true);
                airTemp.setIsUsage(false);
                airTemp.setLoggingTime(300);
                airTemp.setNickname("air-temp");
                airTemp.setShowAble(true);
                airTemp.setTagDescription("에어 온도");
                airTemp.setUnit(null);
                airTemp.setTagName(deviceUnit + "AIR_TEMP");
                airTemp.setUnitConversion(null);
                tags.add(airTemp);
                break;
            case "전력":
                Tag elect = new Tag();
                elect.setIsAlarm(false);
                elect.setIsTrend(false);
                elect.setIsUsage(true);
                elect.setLoggingTime(300);
                elect.setNickname("air-temp");
                elect.setShowAble(true);
                elect.setTagDescription("에어 온도");
                elect.setUnit("kWh");
                elect.setTagName(deviceUnit + "AIR_TEMP");
                elect.setUnitConversion(null);
                tags.add(elect);
                break;
        }
        return tags;
    }
    @Override
    public Boolean createDevice(DeviceInsertDto deviceInsert) {
        return null;
    }

    @Override
    public Boolean updateCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";
        Device seletedDevice = deviceDslRepository.getOneByIdJoinGroupSchedule(compressorInsertDto.getId());


//        // 스케줄 생성 및 그룹과 연동
//        // 스케줄 만 생성
        Schedule newSchedule = seletedDevice.getGroup().getSchedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(true);
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        newSchedule.setMax(scheduleDto.getMax());
        newSchedule.setMin(scheduleDto.getMin());
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());
//        // 요일 관계 생성
        newSchedule.setDayOfWeekMappers(new HashSet<>());
        List<Long> dayOfWeekMapperIds = dayOfWeekMapperDslRepository.findAllByScheduleId(newSchedule.getId());

        dayOfWeekMapperDslRepository.deleteByIdIn(dayOfWeekMapperIds);
        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);

        List<DayOfWeekMapper> newDayOfWeekMappers = new ArrayList<>();
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newSchedule.getDayOfWeekMappers().add(dayOfWeekMapper);
        }
        // 요일 끝

        // 주차 관계 생성
        newSchedule.setWeekMappers(new HashSet<>());
        List<Long> allByScheduleId = weekMapperDslRepository.findAllByScheduleId(newSchedule.getId(), null);
        weekMapperDslRepository.deleteByIdIn(allByScheduleId);
        List<Long> weekIds = scheduleDto.getWeeks().stream()
                .map(WeekDto::getId)
                .collect(toList());
        List<Week> weeks = weekDataRepository.findAllByIdIn(weekIds);

        List<WeekMapper> newWeekMappers = new ArrayList<>();
        for (Week week: weeks) {
            WeekMapper weekMapper = new WeekMapper();
            weekMapper.setWeek(week);
            weekMapper.setSchedule(newSchedule);
            newWeekMappers.add(weekMapper);
        }


        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
        newSchedule.setWeekMappers(new HashSet<>(newWeekMappers));
        scheduleDataRepository.save(newSchedule);

//        // 그룹 생성 및 부모 그룹 세팅
        Group newGroup = seletedDevice.getGroup();
        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());
        newGroup.setParent(parentGroup);
        newGroup.setName(compressorInsertDto.getName());
        newGroup.setType(typeName);
        newGroup.setLevel(2);
        groupDataRepository.save(newGroup);

        // 디바이스 생성 및 그룹과 연
        Equipment selectedEquipoment = equipmentDslRepository.getOneByType(typeName);
        seletedDevice.setName(compressorInsertDto.getName());
        seletedDevice.setEquipment(selectedEquipoment);
        deviceDataRepository.save(seletedDevice);
//
        return true;
    }

    @Override
    public Boolean updateDevice(DeviceInsertDto deviceInsert) {
        return null;
    }

}
