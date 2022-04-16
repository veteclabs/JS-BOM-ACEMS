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
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    private final DeviceDataRepository deviceDataRepository;


    private final DayOfWeekMapperDataRepository dayOfWeekMapperDataRepository;
    private final WeekMapperDataRepository weekMapperDataRepository;

    public Boolean createGruops(GroupInsertDto groupInsertDto) {
        Group newGroup = new Group();
        newGroup.setName(groupInsertDto.getName());
        newGroup.setLevel(1);
        newGroup.setType(GroupType.GROUP);
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
        List<Week> weeks = weekDataRepository.findAllByIdIn(Arrays.asList(new Long[]{1L, 2L, 3L, 4L, 5L}));
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
        List<Long> dayOfWeekMapperIds = dayOfWeekMapperDslRepository.findAllByScheduleId(newGroup.getSchedule().getId());
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

        List<WeekMapper> weekMappers = weekMapperDslRepository.findAllByWeekIdsAndScheduleId(weekIds, groupInsertDto.getSchedule().getId());
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
                        .filter(k -> k.getWeek().getId().equals(t.getId()))
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

        if (!isNull(groupDtos)) {
            for (GroupDto groupDto : groupDtos) {
                List<Long> newDeviceIds = null;
                List<Long> newCompressorIds = null;
                if (!isNull(groupDto.getDeviceList())) {
                    newDeviceIds = groupDto.getDeviceList().stream()
                            .map(t -> t.getId())
                            .collect(toList());
                }
                if (!isNull(groupDto.getAirCompressors())) {
                    newCompressorIds = groupDto.getAirCompressors().stream()
                            .map(t -> t.getId())
                            .collect(toList());
                }
                Group group = groupDslRepository.getOneJoinChildsAndDevicesById(groupDto.getId());


                List<Group> ordCompressors = new ArrayList<>(group.getChildren());
                List<Device> ordDevices = new ArrayList<>(group.getDeviceSet());
                List<Group> newCompressors = groupDslRepository.findAllByIds(newCompressorIds);
                List<Device> newDevices = deviceDslRepository.findAllByIds(newDeviceIds);
                List<Group> tempCompressors = new ArrayList<>();

                newCompressors.forEach(t->tempCompressors.add(t));
                tempCompressors.removeAll(ordCompressors);
                ordCompressors.forEach(t -> t.setParent(null));
                ordDevices.forEach(t -> t.setGroup(null));
                List<Order> orders = orderDslRepository.findAllByIds(tempCompressors.stream()
                        .map(t->t.getId()).collect(Collectors.toList()));
                orderDataRepository.deleteAll(orders);

                groupDataRepository.save(group);
                groupDataRepository.saveAll(ordCompressors);


                group.setChildren(new HashSet<>(newCompressors));
                newCompressors.forEach(t -> t.setParent(group));
                group.setDeviceSet(new HashSet<>(newDevices));
                newDevices.forEach(t -> t.setGroup(group));

                groupDataRepository.save(group);
                groupDataRepository.saveAll(newCompressors);
                deviceDataRepository.saveAll(newDevices);
                updateGroups(groupDto.getAirCompressors());
            }
            return true;
        }
        return true;
    }
    public Boolean deleteGroups(List<Long> groupIds) {
        List<Group> groups = groupDslRepository.findAllByIds(groupIds);
        List<Schedule> schedules = new ArrayList<>();
        groups.forEach(t->{
            if(!isNull(t.getSchedule())) {
                Schedule schedule = t.getSchedule();
                schedules.add(schedule);
                if(!isNull(schedule.getDayOfWeekMappers())) {
                    dayOfWeekMapperDataRepository.deleteAllInBatch(schedule.getDayOfWeekMappers());
                }
                if(!isNull(schedule.getWeekMappers())) {
                    schedule.getWeekMappers().forEach(z->{
                        if(!isNull(z.getOrders())) {
                            orderDataRepository.deleteAllInBatch(z.getOrders());
                        }
                    });
                    weekMapperDataRepository.deleteAllInBatch(schedule.getWeekMappers());
                }

            }
            if(!isNull(t.getChildren())) {
                t.getChildren().forEach(k -> {
                    k.setParent(null);
                });
            }
        });
        groupDataRepository.deleteAllInBatch(groups);
        scheduleDataRepository.deleteAllInBatch(schedules);
        return true;
    }
}
