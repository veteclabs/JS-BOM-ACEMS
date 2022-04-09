package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.dto.week.WeekGroupDto;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.markcha.ems.domain.QWeekMapper.weekMapper;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl {
    private final GroupDslRepositoryImpl groupDslRepository;
    private final GroupDataRepository groupDataRepository;
    private final DayOfWeekDataRepository dayOfWeekDataRepository;
    private final DayOfWeekMapperDslRepositoryImpl dayOfWeekMapperDslRepository;
    private final WeekMapperDslRepositoryImpl weekMapperDslRepository;
    private final WeekDataRepository weekDataRepository;
    private final ScheduleDataRepository scheduleDataRepository;
    private final OrderDataRepository orderDataRepository;

    public Boolean createGruops(GroupInsertDto groupInsertDto) {
        Group newGroup = new Group();
        newGroup.setName(groupInsertDto.getName());
        newGroup.setLevel(1);
        newGroup.setType("group");
        Schedule newSchedule = new Schedule();
        ScheduleDto scheduleDto = groupInsertDto.getSchedule();
        newSchedule.setIsGroup(true);
        newSchedule.setIsActive(true);
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        newSchedule.setMax(scheduleDto.getMax());
        newSchedule.setMin(scheduleDto.getMin());
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());
        newSchedule.getGroups().add(newGroup);
        newGroup.setSchedule(newSchedule);

        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());

        List<DayOfWeekMapper> newDayOfWeekMappers = new ArrayList<>();
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        for (DayOfWeek dayOfWeek: dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newDayOfWeekMappers.add(dayOfWeekMapper);
        }
        // 요일 끝

        // 주차 관계 생성
        List<Week> weeks = weekDataRepository.findAllByIdIn(Arrays.asList(new Long[]{1L, 2L, 3L, 4L}));
        List<WeekMapper> newWeekMappers = new ArrayList<>();
        for (Week week: weeks) {
            WeekMapper weekMapper = new WeekMapper();
            weekMapper.setWeek(week);
            weekMapper.setSchedule(newSchedule);
            newWeekMappers.add(weekMapper);
        }

        newSchedule.setWeekMappers(new HashSet<>(newWeekMappers));
        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
        scheduleDataRepository.save(newSchedule);
        groupDataRepository.save(newGroup);


        return true;
    }

    public Boolean updateCompressor(GroupInsertDto groupInsertDto) {
        String typeName = "compressor";
//        Device seletedDevice = deviceDslRepository.getOneByIdJoinGroupSchedule(compressorInsertDto.getId());


//        // 스케줄 생성 및 그룹과 연동
//        // 스케줄 만 생성
        Schedule newSchedule = new Schedule();
        ScheduleDto scheduleDto = groupInsertDto.getSchedule();
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
        System.out.println(dayOfWeekMapperIds);
        dayOfWeekMapperDslRepository.deleteByIdIn(dayOfWeekMapperIds);
        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        System.out.println(dayOfWeeks);
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
        List<Long> weekIds = new ArrayList<>();
        scheduleDto.getWeekDevices().forEach(k->{
            if (!isNull(k.getWorking()) && k.getWorking().size() != 0){
                k.getId(); // 윅에 해당하는 모든 order 가져오기
                scheduleDto.getId();
            }
        });
        List<WeekMapper> weekMappers = weekMapperDslRepository.findAllByWeekIdsAndScheduleId(weekIds, scheduleDto.getId());
        List<Long> orderIds = new ArrayList<>();
        weekMappers.forEach(t->t.getOrders().forEach(k->orderIds.add(k.getId())));
//        orderDataRepository.deleteAllByIdInBatch(orderIds);
        // delete all orderIds
        //
        // // //
        List<WeekGroupDto> weekGroupDtos = scheduleDto.getWeekDevices();
        List<Order> newOrders = new ArrayList<>();
        weekGroupDtos.forEach(t->{
            int order = 1;
            for (CompressorSimpleDto compressorSimpleDto : t.getWorking()) {
                Order newOrder = new Order();
                newOrder.setGroup(compressorSimpleDto.getSaveGroup());
                newOrder.setOrder(order);
                Week a = t.getSavedWeek();
                WeekMapper weekMapper = weekMappers.stream()
                        .filter(k -> k.getWeek() == t.getSavedWeek())
                        .findFirst().get();

                newOrder.setWeekMapper(weekMapper);
                newOrders.add(newOrder);
                order = order + 1;
            }
        });
//        orderDataRepository.saveAll(newOrders);
//        List<Week> weeks = weekDataRepository.findAllByIdIn(weekIds);
//        List<WeekMapper> newWeekMappers = new ArrayList<>();
//        for (Week week: weeks) {
//            WeekMapper weekMapper = new WeekMapper();
//            weekMapper.setWeek(week);
//            weekMapper.setSchedule(newSchedule);
//            weekMapper.set
//            newWeekMappers.add(weekMapper);
//        }
//
//
//        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
//        newSchedule.setWeekMappers(new HashSet<>(newWeekMappers));
//        scheduleDataRepository.save(newSchedule);
//
////        // 그룹 생성 및 부모 그룹 세팅
//        Group newGroup = seletedDevice.getGroup();
//        Group parentGroup = groupDslRepository.getOneById(compressorInsertDto.getGroupId());
//        newGroup.setParent(parentGroup);
//        newGroup.setName(compressorInsertDto.getName());
//        newGroup.setType(typeName);
//        newGroup.setLevel(2);
//        groupDataRepository.save(newGroup);
//
//        // 디바이스 생성 및 그룹과 연
//        Equipment selectedEquipoment = equipmentDslRepository.getOneByType(typeName);
//        seletedDevice.setName(compressorInsertDto.getName());
//        seletedDevice.setEquipment(selectedEquipoment);
//        deviceDataRepository.save(seletedDevice);
//
        return true;
    }

}
