package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.CompressorSimpleDto;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.dto.week.WeekGroupDto;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
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
    private final OrderDslRepositoryImpl orderDslRepository;
    private final DeviceDslRepositoryImpl deviceDslRepository;
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
        Group newGroup = groupDataRepository.getById(groupInsertDto.getId());
        newGroup.setName(groupInsertDto.getName());
        Schedule newSchedule = newGroup.getSchedule();
        ScheduleDto scheduleDto = groupInsertDto.getSchedule();
        newSchedule.setIsGroup(true);
        newSchedule.setIsActive(scheduleDto.getIsActive());
        newSchedule.setInterval(30);
        newSchedule.setType("interval");
        newSchedule.setMax(scheduleDto.getMax());
        newSchedule.setMin(scheduleDto.getMin());
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());
        newSchedule.getGroups().add(newGroup);
        newGroup.setSchedule(newSchedule);
//        // 요일 관계 생성
        newSchedule.setDayOfWeekMappers(new HashSet<>());
        List<Long> dayOfWeekMapperIds = dayOfWeekMapperDslRepository.findAllByScheduleId(groupInsertDto.getSchedule().getId());
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
        List<Long> weekIds = new ArrayList<>();
        scheduleDto.getWeekDevices().forEach(k->{
            if (!isNull(k.getWorking())) {
                weekIds.add(k.getId());
            }
        });
        List<WeekMapper> weekMappers = weekMapperDslRepository.findAllByWeekIdsAndScheduleId(weekIds, scheduleDto.getId());
        List<Long> orderIds = new ArrayList<>();
        weekMappers.forEach(t->t.getOrders().forEach(k->orderIds.add(k.getId())));
        orderDataRepository.deleteAllByIdInBatch(orderIds);
        List<WeekGroupDto> weekGroupDtos = scheduleDto.getWeekDevices();
        List<Group> workingGroups = groupDslRepository.findAllChildGroupsById(groupInsertDto.getId());
        List<Order> newOrders = new ArrayList<>();
        weekGroupDtos.forEach(t->{
            int order = 1;
            for (CompressorSimpleDto compressorSimpleDto : t.getWorking()) {
                Order newOrder = new Order();
                Group group = workingGroups.stream().filter(k->k.getId() == compressorSimpleDto.getId())
                        .findFirst().get();
                newOrder.setGroup(group);
                newOrder.setOrder(order);
                WeekMapper weekMapper = weekMappers.stream()
                        .filter(k -> k.getWeek().getId() == t.getId())
                        .findFirst().get();
                newOrder.setWeekMapper(weekMapper);
                newOrders.add(newOrder);
                order = order + 1;
            }
        });
        orderDataRepository.saveAll(newOrders);

        newSchedule.setDayOfWeekMappers(new HashSet<>(newDayOfWeekMappers));
        scheduleDataRepository.save(newSchedule);
        groupDataRepository.save(newGroup);

        return true;
    }
    public Boolean updateGroups(List<GroupDto> groupDtos) {
        List<Long> compressorDeviceIds = new ArrayList<>();
        List<Long> compressorIds = new ArrayList<>();
        groupDtos.forEach(t-> {
                    t.getAirCompressors().forEach(k -> {
                            compressorDeviceIds.addAll(k.getDevices().stream().map(g->g.getId()).collect(toList()));
                            compressorIds.add(k.getId());
                        });

                });
        List<Group> compressors = groupDslRepository.findAllByIds(compressorIds);
        List<Device> devices = deviceDslRepository.findAllByIds(compressorDeviceIds);

        groupDtos.forEach(t-> {
            t.getAirCompressors().forEach(k -> {
                Group compressor = compressors.stream().filter(c -> c.getId().equals(k.getId())).findFirst().get();
                k.getDevices().forEach(g->{
                    Device device = devices.stream().filter(c -> c.getId().equals(g.getId())).findFirst().get();
                    device.setGroup(compressor);
                });
            });

        });

//        List<Device> compressorDevices = device

//        List<Long> groupIds = GroupDtos.stream()
//                .map(t->t.getId())
//                .collect(toList());
//
//        List<Long> compressorIds = new ArrayList<>();
//        GroupDtos.forEach(t->
//                compressorIds.addAll(t.getAirCompressors().stream().map(k->k.getId()).collect(toList())));

        return true;
    }
}
