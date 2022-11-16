package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController.CompressorInsertDto;
import com.markcha.ems.controller.WebAccessController;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.alarm.AlarmDto;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.AirCompressorDto;
import com.markcha.ems.dto.device.ComponentsDto;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.mapper.alarm.AlarmMapDto;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.markcha.ems.service.InsertSampleData;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

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
    private final WebaccessApiServiceImpl webaccessApiService;
    private final OrderDataRepository orderDataRepository;
    private final OrderDslRepositoryImpl orderDslRepository;
    private final AlarmDataRepository alarmDataRepository;


    public Boolean createCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";

        // 스케줄 생성 및 그룹과 연동
        // 스케줄 만 생성
        Schedule newSchedule = new Schedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(isNull(scheduleDto) ? false : scheduleDto.getIsActive());
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        if (!isNull(scheduleDto)) {
            newSchedule.setMax(new Double(scheduleDto.getMax().toString()));
            newSchedule.setMin(new Double(scheduleDto.getMin().toString()));
            newSchedule.setStartTime(scheduleDto.getStartTime());
            newSchedule.setStopTime(scheduleDto.getStopTime());
        }
        newSchedule.setUpdated(LocalDateTime.now());

        // 요일 관계 생성
        List<Long> dayOfWeekIds = null;
        if (!isNull(scheduleDto)) scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        List<DayOfWeekMapper> newDayOfWeekMappers = new ArrayList<>();
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
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

        for (Week week : weeks) {
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


        // 디바이스 생성 및 그룹과 연동
        Device newDevice = new Device();
        Equipment selectedEquipment = equipmentDslRepository.getOneByType(AIR_COMPRESSOR);
        newDevice.setName(compressorInsertDto.getName());
        newDevice.setEquipment(selectedEquipment);
        newDevice.setGroup(newGroup);
        newDevice.setRemark(compressorInsertDto.getUnitId().toString());
        Device save = deviceDataRepository.save(newDevice);
        List<Tag> tags = insertSampleData.createTags(isNull(compressorInsertDto.getEquipmentId()) ? compressorInsertDto.getEquipment().getEquipmentId() : compressorInsertDto.getEquipmentId(), save, compressorInsertDto.getUnitId());

        List<TagDto> tagDtos = tags.stream()
                .map(TagDto::new)
                .collect(toList());
        List<TagDto> minMaxTag = null;
        if (!isNull(scheduleDto)) {
            tagDtos.stream()
                    .filter(t -> {
                        boolean isMax = t.getType().equals("COMP_StopPre");
                        boolean isMin = t.getType().equals("COMP_StartPre");
                        if (isMax) {
                            t.setValue(scheduleDto.getMax());
                        }
                        if (isMin) {
                            t.setValue(scheduleDto.getMin());
                        }
                        return isMax || isMin;
                    })
                    .collect(toList());

            if(!isNull(minMaxTag)) webaccessApiService.setTagValues(minMaxTag);
        }


        newDevice.setTags(new HashSet<>(tags));
        deviceDataRepository.save(save);
        return true;
    }

    @Async
    @Transactional
    public Boolean updateCompressor(CompressorInsertDto compressorInsertDto) {
        String typeName = "compressor";
        Device seletedDevice = deviceDslRepository.getOneByIdJoinGroupSchedule(compressorInsertDto.getId());


        // 스케줄 생성 및 그룹과 연동
        // 스케줄 만 생성
        Schedule newSchedule = seletedDevice.getGroup().getSchedule();
        ScheduleDto scheduleDto = compressorInsertDto.getSchedule();
        newSchedule.setIsGroup(false);
        newSchedule.setIsActive(isNull(scheduleDto)? false : scheduleDto.getIsActive());
        newSchedule.setInterval(5);
        newSchedule.setType("interval");
        if(!isNull(scheduleDto)) {
            newSchedule.setMax(new Double(scheduleDto.getMax().toString()));
            newSchedule.setMin(new Double(scheduleDto.getMin().toString()));
            newSchedule.setStartTime(scheduleDto.getStartTime());
            newSchedule.setStopTime(scheduleDto.getStopTime());
        }
        newSchedule.setUpdated(LocalDateTime.now());


        // 요일 관계 생성
        newSchedule.getDayOfWeekMappers().clear();
        List<Long> dayOfWeekIds = null;
        if(!isNull(scheduleDto)) scheduleDto.getDayOfWeeks().stream()
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

        // 그룹 생성 및 부모 그룹 세팅
        Group newGroup = seletedDevice.getGroup();
        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());
        newGroup.setParent(parentGroup);
        newGroup.setName(compressorInsertDto.getName());
        newGroup.setType(GroupType.OBJECT);
        newGroup.setLevel(2);
        groupDataRepository.save(newGroup);

        // 디바이스 생성 및 그룹과 연동
        Equipment selectedEquipment = equipmentDslRepository.getOneById(isNull(compressorInsertDto.getEquipmentId()) ?  compressorInsertDto.getEquipment().getEquipmentId(): compressorInsertDto.getEquipmentId());
        seletedDevice.setName(compressorInsertDto.getName());
        seletedDevice.setEquipment(selectedEquipment);
        List<Alarm> alarms = seletedDevice.getTags().stream()
                .map(t -> t.getAlarms())
                .collect(toList())
                .stream().flatMap(List::stream)
                .collect(toList());
        alarms.forEach(t->t.setTag(null));
        alarmDataRepository.saveAll(alarms);
        tagDataRepository.deleteAllInBatch(seletedDevice.getTags());
        List<Tag> tags = insertSampleData.createTags(isNull(compressorInsertDto.getEquipmentId()) ?  compressorInsertDto.getEquipment().getEquipmentId(): compressorInsertDto.getEquipmentId(), seletedDevice, compressorInsertDto.getUnitId());
        seletedDevice.setTags(new HashSet<>(tags));
        List<TagDto> tagsDto = tags.stream()
                .map(TagDto::new)
                .collect(toList());
        List<TagDto> minMaxTag = null;
        if (!isNull(scheduleDto)) {
            tagsDto.stream()
                    .filter(t -> {
                        boolean isMax = t.getType().equals("COMP_StopPre");
                        boolean isMin = t.getType().equals("COMP_StartPre");
                        if (isMax) {
                            t.setValue(scheduleDto.getMax());
                        }
                        if (isMin) {
                            t.setValue(scheduleDto.getMin());
                        }
                        return isMax || isMin;
                    })
                    .collect(toList());

            if(!isNull(minMaxTag)) webaccessApiService.setTagValues(minMaxTag);
        }
        if(!isNull(minMaxTag)) webaccessApiService.setTagValues(minMaxTag);
        deviceDataRepository.save(seletedDevice);
        List<Order> allByDeviceId = orderDslRepository.findAllByDeviceId(compressorInsertDto.getId());
        orderDataRepository.deleteAllInBatch(allByDeviceId);
        return true;
    }

    public void deleteAllById(List<Long> ids) {
        List<Device> compressors = deviceDslRepository.findAllCompressorsByIds(AIR_COMPRESSOR, ids);
        List<Order> allByDeviceId = orderDslRepository.findAllByDeviceIds(ids);
        List<Device> orphanDevices = new ArrayList<>();
        List<DayOfWeekMapper> dayOfWeekMappers = new ArrayList<>();
        List<WeekMapper> weekMappers = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();
        List<Alarm> alarms = new ArrayList<>();

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
                t.getTags().forEach(k->{
                    alarms.addAll(k.getAlarms());
                });
            }
        });
        orphanDevices.forEach(t->t.setGroup(null));
        alarmDataRepository.deleteAllInBatch(alarms);
        weekMapperDataRepository.deleteAllInBatch(weekMappers);
        alarmDataRepository.deleteAllInBatch(alarms);
        tagDataRepository.deleteAllInBatch(tags);
        deviceDataRepository.deleteAllInBatch(compressors);
        deviceDataRepository.saveAll(orphanDevices);
        orderDataRepository.deleteAllInBatch(allByDeviceId);
        groupDataRepository.deleteAllInBatch(groups);
        scheduleDataRepository.saveAll(schedules);
        scheduleDataRepository.deleteAllInBatch(schedules);
    }

    public List<Group> findAllJoinAlarm(BooleanExpression groupEqId, ComponentsDto components) {

        List<Group> compressors = deviceDslRepository.findAllCompressorsJoinEquipment(AIR_COMPRESSOR, groupEqId);
        List<Long> compIds = compressors.stream()
                .map(t -> t.getId())
                .collect(toList());
        List<Device> devices = deviceDslRepository.getDeviceByGroupIds(compIds);
        List<Tag> tags = deviceDslRepository.getDeviceByDeviceIds(devices.stream()
                .map(t -> t.getId()).collect(toList()), components.getComponents());
        Map<Long, Set<Tag>> grouppingTag = tags.stream()
                .collect(groupingBy(t -> t.getDevice().getId(), toSet()));
        devices.stream().forEach(t->{
            t.setTags(grouppingTag.get(t.getId()));
        });
        List<String> tagNames = tags.stream()
                .map(t->t.getTagName()).collect(toList());
        Map<String, Object> tagValuesV2 = webaccessApiService.getTagValuesV2(tagNames);
        devices.forEach(t->{
            if(!isNull(t.getTags())) {
                t.getTags().forEach(k->k.setValue(tagValuesV2.get(k.getTagName())));
            }
        });

        Map<Long, List<Device>> groupingDevices = devices.stream()
                .collect(groupingBy(t->t.getGroup().getId()));
        compressors.forEach(t->{
            if(!isNull(groupingDevices.get(t.getId()))) {
                t.setDeviceSet(new HashSet<>(groupingDevices.get(t.getId())));
            }
        });

        return compressors;
    }
    @Async
    public void controllPowerAsync(Integer intever, Map<String, TagDto> tagMap, Integer powerCode) throws InterruptedException {
        tagMap.get("COMP_Power").setValue(powerCode);
        webaccessApiService.setTagValueV2(tagMap.get("COMP_Power"));

    }

}
