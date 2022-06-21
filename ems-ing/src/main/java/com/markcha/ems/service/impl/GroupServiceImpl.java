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
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.dto.week.WeekGroupDto;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.markcha.ems.domain.QDayOfWeekMapper.dayOfWeekMapper;
import static com.markcha.ems.domain.QGroup.group;
import static com.markcha.ems.domain.QOrder.order1;
import static com.markcha.ems.domain.QSchedule.schedule;
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
    private final WebaccessApiServiceImpl webaccessApiService;

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
        newSchedule.setMax(new Double(scheduleDto.getMax().toString()));
        newSchedule.setMin(new Double(scheduleDto.getMin().toString()));
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
    public List<RemoveDayOfWeekDto> checkSchedule(GroupInsertDto groupInsertDto) {
        List<RemoveDayOfWeekDto> removeDayOfWeekDtos = new ArrayList<>();
        List<Long> dayOfWeekIds = groupInsertDto.getSchedule().getDayOfWeeks().stream()
                .map(s -> s.getId()).collect(toList());
        for (Group workingGroup : groupDslRepository.findAllChildGroupsById(groupInsertDto.getId())) {
            RemoveDayOfWeekDto removeDayOfWeekDto = new RemoveDayOfWeekDto();
            removeDayOfWeekDto.setName(workingGroup.getName());
            List<DayOfWeek> childDayOfWeeks = workingGroup.getSchedule().getDayOfWeekMappers().stream()
                    .map(k -> k.getDayOfWeek())
                    .collect(toList());
            childDayOfWeeks.forEach(k -> {
                    if(dayOfWeekIds.contains(k.getId())) {
                        removeDayOfWeekDto.getDayOfWeekName().add(k.getName());
                    }
            });
            if(removeDayOfWeekDto.getDayOfWeekName().size() != 0) removeDayOfWeekDtos.add(removeDayOfWeekDto);
        }
        return removeDayOfWeekDtos;
    }
    @Setter
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class  RemoveDayOfWeekDto {
        private String name;
        private List<String> dayOfWeekName = new ArrayList<>();
    }
    public String updateCompressor(GroupInsertDto groupInsertDto) {
        Group newGroup = groupDslRepository.getOneJoinScheduleById(groupInsertDto.getId());
        newGroup.setName(groupInsertDto.getName());
        Schedule newSchedule = newGroup.getSchedule();
        ScheduleDto scheduleDto = groupInsertDto.getSchedule();
        newSchedule.setIsGroup(true);
        newSchedule.setIsActive(scheduleDto.getIsActive());
        newSchedule.setInterval(5);
        newSchedule.setType("interval");
        newSchedule.setMax(new Double(scheduleDto.getMax().toString()));
        newSchedule.setMin(new Double(scheduleDto.getMin().toString()));
        newSchedule.setStartTime(scheduleDto.getStartTime());
        newSchedule.setStopTime(scheduleDto.getStopTime());
        newSchedule.setUpdated(LocalDateTime.now());
        newSchedule.getGroups().add(newGroup);
        newGroup.setSchedule(newSchedule);



//        // 요일 관계 생성
        newSchedule.getDayOfWeekMappers().clear();
        List<Long> dayOfWeekIds = scheduleDto.getDayOfWeeks().stream()
                .map(DayOfWeekDto::getId)
                .collect(toList());
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(dayOfWeekIds);
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newSchedule.getDayOfWeekMappers().add(dayOfWeekMapper);
        }
        scheduleDataRepository.save(newSchedule);
        // 요일 끝

        // 주차 관계 생성

        List<WeekGroupDto> weekDevices = scheduleDto.getWeekDevices();
        List<Long> weekIds = weekDevices.stream()
                .map(t -> t.getId())
                .collect(toList());

        List<Group> workingGroups = groupDslRepository.findAllChildGroupsById(groupInsertDto.getId());
        for (Group workingGroup : workingGroups) {
            List<DayOfWeek> childDayOfWeeks = workingGroup.getSchedule().getDayOfWeekMappers().stream()
                    .map(k -> k.getDayOfWeek())
                    .collect(toList());
            List<DayOfWeekMapper> dayOfWeekMappers = childDayOfWeeks.stream()
                    .filter(k -> {
                        if(groupInsertDto.getSchedule().getDayOfWeeks().stream()
                                .map(s->s.getId()).collect(toList()).contains(k.getId())) {
                            return false;
                        } else {
                            return true;
                        }
                    })
                    .map(k-> new DayOfWeekMapper().builder()
                            .dayOfWeek(k)
                            .schedule(workingGroup.getSchedule())
                            .build())
                    .collect(toList());
            workingGroup.getSchedule().getDayOfWeekMappers().clear();
            workingGroup.getSchedule().getDayOfWeekMappers().addAll(dayOfWeekMappers);
        }


        List<WeekMapper> weekMappers1 = weekMapperDslRepository.findAllByWeekIdsAndScheduleId(weekIds, newSchedule.getId());
        weekMappers1.forEach(t->{
            t.getOrders().clear();
            weekDevices.forEach(k->{
                if(k.getId().equals(t.getWeek().getId())) {
                    int orderNum = 1;
                    for (CompressorSimpleDto compressorSimpleDto : k.getWorking()) {
                        Group group = workingGroups.stream()
                                .filter(z -> z.getId().equals(compressorSimpleDto.getId()))
                                .findFirst()
                                .get();
                        Order newOrder = new Order();
                        newOrder.setGroup(group);
                        newOrder.setOrder(orderNum);
                        newOrder.setWeekMapper(t);
                        t.getOrders().add(newOrder);
                        orderNum++;
                    }
                }
            });
        });

        weekMapperDataRepository.saveAll(weekMappers1);
        groupDataRepository.saveAll(workingGroups);
        groupDataRepository.save(newGroup);
        List<TagDto> minMaxTags = groupDslRepository.findAllChildGruopMaxTags(groupInsertDto.getId()).stream()
                .map(t->{
                    TagDto tag = new TagDto(t);
                    tag.setValue(groupInsertDto.getSchedule().getMax());
                    return tag;
                })
                .collect(toList());
        minMaxTags.addAll(groupDslRepository.findAllChildGruopMinTags(groupInsertDto.getId()).stream()
                .map(t->{
                    TagDto tag = new TagDto(t);
                    tag.setValue(groupInsertDto.getSchedule().getMin());
                    return tag;
                })
                .collect(toList()));
        webaccessApiService.setTagValues(minMaxTags);
        return null;
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

                List<Group> ordCompressors = groupDslRepository.getChildGroups(new ArrayList<>(List.of(groupDto.getId())));

                List<Device> ordDevices = new ArrayList<>(group.getDeviceSet());
                List<Group> newCompressors = groupDslRepository.findAllByIds(newCompressorIds);
                List<Device> newDevices = deviceDslRepository.findAllByIds(newDeviceIds);
                List<Group> deletedCompressor = new ArrayList<>();
                List<Group> deleted2Compressor = new ArrayList<>();
                deletedCompressor.addAll(ordCompressors);
                deleted2Compressor.addAll(newCompressors);
                deleted2Compressor.removeAll(ordCompressors);
                deletedCompressor.removeAll(newCompressors);
                ordCompressors.forEach(t -> t.setParent(null));
                ordDevices.forEach(t -> t.setGroup(null));
                deletedCompressor.addAll(deleted2Compressor);
                List<Order> orders = orderDslRepository.findAllByIds(
                        deletedCompressor.stream()
                                .map(t->t.getId())
                                .collect(Collectors.toList()));

                orderDataRepository.deleteAllInBatch(orders);

                groupDataRepository.save(group);
                groupDataRepository.saveAll(ordCompressors);

                group.setChildren(new HashSet<>(newCompressors));
                newCompressors.forEach(t -> t.setParent(group));
                group.setDeviceSet(new HashSet<>(newDevices));
                newDevices.forEach(t -> t.setGroup(group));

                groupDataRepository.save(group);
                groupDataRepository.saveAll(newCompressors);
                deviceDataRepository.saveAll(newDevices);
//                updateGroups(groupDto.getAirCompressors());
            }
            return true;
        }
        return true;
    }
    public Boolean deleteGroups(List<Long> groupIds) {
        List<Group> groups = groupDslRepository.findAllByIds(groupIds);
        List<Schedule> schedules = new ArrayList<>();
        List<Device> orphanDevices = new ArrayList<>();
        List<Group> orphanGroups = new ArrayList<>();
        List<WeekMapper> weekMappers = new ArrayList<>();
        groups.forEach(t->{
            if(!isNull(t.getSchedule())) {
                Schedule schedule = t.getSchedule();
                schedule.getWeekMappers().forEach(k->{
                    k.getOrders().clear();
                    weekMappers.add(k);
                });
                schedule.getDayOfWeekMappers().clear();
                schedules.add(schedule);

            }
            if(!isNull(t.getChildren())) {
                t.getChildren().forEach(k -> {
                    k.setParent(null);
                });
            }
            if(!isNull(t.getDeviceSet())) {
                t.getDeviceSet().forEach(k->{
                    k.setGroup(null);
                    orphanDevices.add(k);
                });
            }
            t.setDeviceSet(null);
            t.setChildren(null);
        });
        deviceDataRepository.saveAll(orphanDevices);
        groupDataRepository.saveAll(orphanGroups);
        groupDataRepository.deleteAllInBatch(groups);
        weekMapperDataRepository.saveAll(weekMappers);
        weekMapperDataRepository.deleteAllInBatch(weekMappers);
        scheduleDataRepository.saveAll(schedules);
        scheduleDataRepository.deleteAllInBatch(schedules);

        List<Long> childIds = groups.stream()
                .map(pg -> {
                    return pg.getChildren().stream()
                            .map(cg -> cg.getId())
                            .collect(toList());
                }).flatMap(List::stream)
                .collect(toList());

        List<Order> allByDeviceId = orderDslRepository.findAllByDeviceIds(childIds);
        orderDataRepository.deleteAllInBatch(allByDeviceId);
        return true;
    }

    public void unActiveSchedule(Group group) {
        if(!isNull(group.getSchedule())) {
            Schedule schedule = group.getSchedule();
            schedule.setIsActive(false);
            scheduleDataRepository.save(schedule);
        }
        if(!isNull(group.getChildren())) {
            List<Long> broIds = group.getChildren().stream()
                    .map(t -> t.getId())
                    .collect(toList());
            orderDataRepository.deleteAllInBatch(orderDslRepository.findAllByIds(broIds));
            group.getChildren().forEach(t->t.setParent(null));
            group.setChildren(null);
            groupDataRepository.save(group);
        }

    }

    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
