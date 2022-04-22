package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController.CompressorInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.alarm.AlarmDto;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.AirCompressorDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.mapper.alarm.AlarmMapDto;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompressorServiceImpl {
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
    private final TagDataRepository tagDataRepository;

    public Boolean createCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";

        // 스케줄 생성 및 그룹과 연동
        // 스케줄 만 생성
        Schedule newSchedule = new Schedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(compressorInsertDto.getSchedule().getIsActive());
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

        // 주차 관계 생성
        List<WeekMapper> newWeekMappers = new ArrayList<>();

        List<Week> weeks = weekDataRepository.findAllByIdIn(Arrays.asList(new Long[]{1L, 2L, 3L, 4L, 5L, 6L}));

        for (Week week: weeks) {
            WeekMapper weekMapper = new WeekMapper();
            weekMapper.setWeek(week);
            weekMapper.setSchedule(newSchedule);
            newWeekMappers.add(weekMapper);
        }


        newSchedule.setWeekMappers(new HashSet<>(newWeekMappers));
        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
        scheduleDataRepository.save(newSchedule);

        // 그룹 생성 및 부모 그룹 세팅
        Group newGroup = new Group();
        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());

        newGroup.setParent(parentGroup);
        newGroup.setName(compressorInsertDto.getName());
        newGroup.setType(GroupType.OBJECT);
        newGroup.setSchedule(newSchedule);
        newGroup.setLevel(2);
        groupDataRepository.save(newGroup);


        // 디바이스 생성 및 그룹과 연
        Device newDevice = new Device();
        Equipment selectedEquipoment = equipmentDslRepository.getOneByType(AIR_COMPRESSOR);
        newDevice.setName(compressorInsertDto.getName());
        newDevice.setEquipment(selectedEquipoment);
        newDevice.setGroup(newGroup);

        Device save = deviceDataRepository.save(newDevice);
        List<Tag> tags = insertSampleData.createTags(AIR_COMPRESSOR, save);
        newDevice.setTags(new HashSet<>(tags));
        deviceDataRepository.save(save);
        return true;
    }
    public Boolean updateCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";
        Device seletedDevice = deviceDslRepository.getOneByIdJoinGroupSchedule(compressorInsertDto.getId());


//        // 스케줄 생성 및 그룹과 연동
//        // 스케줄 만 생성
        Schedule newSchedule = seletedDevice.getGroup().getSchedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(scheduleDto.getIsActive());
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        newSchedule.setMax(scheduleDto.getMax());
        newSchedule.setMin(scheduleDto.getMin());
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());



//        // 요일 관계 생성
        newSchedule.getDayOfWeekMappers().clear();
        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        Set<DayOfWeekMapper> dayOfWeekMappers = new HashSet<>();
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newSchedule.getDayOfWeekMappers().add(dayOfWeekMapper);
        }

        scheduleDataRepository.save(newSchedule);

//        // 그룹 생성 및 부모 그룹 세팅
        Group newGroup = seletedDevice.getGroup();
        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());
        newGroup.setParent(parentGroup);
        newGroup.setName(compressorInsertDto.getName());
        newGroup.setType(GroupType.OBJECT);
        newGroup.setLevel(2);
        groupDataRepository.save(newGroup);

        // 디바이스 생성 및 그룹과 연
        Equipment selectedEquipoment = equipmentDslRepository.getOneByType(AIR_COMPRESSOR);
        seletedDevice.setName(compressorInsertDto.getName());
        seletedDevice.setEquipment(selectedEquipoment);
        deviceDataRepository.save(seletedDevice);
        return true;
    }

    public void deleteAllById(List<Long> ids) {
        List<Device> compressors = deviceDslRepository.findAllCompressorsByIds(AIR_COMPRESSOR, ids);
        List<Device> orphanDevices = new ArrayList<>();
        List<DayOfWeekMapper> dayOfWeekMappers = new ArrayList<>();
        List<WeekMapper> weekMappers = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();
        compressors.forEach(t->{
            if(!isNull(t.getGroup())) {
                Group group = t.getGroup();
                if(!isNull(group.getDeviceSet())) {
                    group.getDeviceSet().forEach(g->{
                        g.setGroup(null);
                        orphanDevices.add(g);
                    });
                }
                groups.add(group);
                if(!isNull(group.getSchedule())) {
                    Schedule schedule = group.getSchedule();
                    schedule.getDayOfWeekMappers().clear();
                    schedules.add(schedule);
                    if(!isNull(schedule.getWeekMappers())) weekMappers.addAll(schedule.getWeekMappers());
                }
            }
            if(!isNull(t.getTags())) {
                tags.addAll(t.getTags());
            }
        });
        orphanDevices.forEach(t->t.setGroup(null));
        weekMapperDataRepository.deleteAllInBatch(weekMappers);
        tagDataRepository.deleteAllInBatch(tags);
        deviceDataRepository.deleteAllInBatch(compressors);
        deviceDataRepository.saveAll(orphanDevices);
        groupDataRepository.deleteAllInBatch(groups);
        scheduleDataRepository.saveAll(schedules);
        scheduleDataRepository.deleteAllInBatch(schedules);
    }

    public List<AirCompressorDto> findAllJoinAlarm() {
        List<AirCompressorDto> compressors = deviceDslRepository.findAllCompressorsJoinEquipment(AIR_COMPRESSOR).stream()
                .map(AirCompressorDto::new)
                .collect(toList());
        List<String> tagNames = new ArrayList<>();
        compressors.stream().forEach(t -> t.getTags().forEach(k->tagNames.add(k.getTagName())));
//        AlarmMapDto alarmMapDto = new AlarmMapDto(tagNames);
//        Map<String, List<AlarmDto>> grouppingAlarm = alarmMapper.getTodayAlarmState(alarmMapDto).stream()
//                .map(AlarmDto::new)
//                .collect(groupingBy(t -> t.getTagName()));

//        compressors.forEach(t->{
//            t.getTags().forEach(k->{
//                List<AlarmDto> alarmDtos = grouppingAlarm.get(k.getTagName());
//                if(!isNull(alarmDtos) && alarmDtos.size() != 0) {
//                    t.setAlarm(true);
//                    t.setAlarmMention(alarmDtos.get(0).getDescription());
//                }
//                k.setAlarms(alarmDtos);
//            });
//        });

        return compressors;
    }

    public AirCompressorDto getOneJoinAlarm(Long id) {
        AirCompressorDto compressor = new AirCompressorDto(deviceDslRepository.getOneCompressorsJoinEquipment(id, AIR_COMPRESSOR));
        List<String> tagNames = new ArrayList<>();
//        compressor.getTags().forEach(k->tagNames.add(k.getTagName()));
//        AlarmMapDto alarmMapDto = new AlarmMapDto(tagNames);
//        Map<String, List<AlarmDto>> grouppingAlarm = alarmMapper.getTodayAlarmState(alarmMapDto).stream()
//                .map(AlarmDto::new)
//                .collect(groupingBy(t -> t.getTagName()));

//        compressor.getTags().forEach(k->{
//                List<AlarmDto> alarmDtos = grouppingAlarm.get(k.getTagName());
//                if(!isNull(alarmDtos) && alarmDtos.size() != 0) {
//                    compressor.setAlarm(true);
//                    compressor.setAlarmMention(alarmDtos.get(0).getDescription());
//                }
//                k.setAlarms(alarmDtos);
//            });

        return compressor;
    }
}
