package com.markcha.ems.config;



import com.markcha.ems.controller.ScheduleController;
import com.markcha.ems.domain.*;
import com.markcha.ems.repository.AlarmDataRepository;
import com.markcha.ems.repository.DayOfWeekDataRepository;
import com.markcha.ems.repository.TripDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.tag.TagDslRepositoryIml;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;
@EnableAsync
@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final ScheduleDslRepositoryImpl scheduleDslRepository;
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final OrderDslRepositoryImpl orderDslRepository;
    private final DayOfWeekDataRepository dayOfWeekDataRepository;
    private final AlarmDataRepository alarmDataRepository;
    private final TagDslRepositoryIml tagDslRepositoryIml;
    private final WebaccessApiServiceImpl webaccessApiService;
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final TripDataRepository tripDataRepository;
    private final GroupDslRepositoryImpl groupDslRepository;


    private static Set<Tag> savedTags = null;
    private static Boolean alarmInsert = false;

    private static Map<Long, Timer> tasks = new HashMap<>();

    private final static List<String> actTripCode = List.of(
            "COMP_ActTripCode", "COMP_ActWarCode",
            "COMP_TripCode", "COMP_WarningCode");
    @Scheduled(cron="*/5 * * * * *")
    public void scheduleFixedRateTask() {

        List<ScheduleController.ScheduleSimpleDto> schedules = scheduleDslRepository.findAllCoreSchedule().stream()
                .map(ScheduleController.ScheduleSimpleDto::new)
                .collect(toList());
        ArrayList<Long> longs = new ArrayList<>(tasks.keySet());
        ArrayList<Long> collect = new ArrayList<>(schedules.stream().map(s -> s.getId()).collect(toList()));
        List<Long> difference = difference(collect, longs);
        schedules.forEach(schedule->{
            if(schedule.getIsActive() && difference.contains(schedule.getId())) {
                Timer timer = new Timer();
                Integer intever = 1000;
                ScheduleTask scheduleTask = new ScheduleTask(
                        groupDslRepository,
                        scheduleDslRepository,
                        orderDslRepository,
                        groupDynamicRepository,
                        webaccessApiService,
                        tagDslRepositoryIml,
                        alarmDataRepository);
                scheduleTask.setScheduleId(schedule.getId());
                timer.schedule(scheduleTask,100, 100);
                tasks.put(schedule.getId(), timer);
            }
        });
        ArrayList<Long> taskRemover = difference(longs, collect);
        if(!isNull(taskRemover)) {
            taskRemover.forEach(t -> {
                tasks.get(t).cancel();
                tasks.remove(t);
            });
        }

    }

    private Map<Tag, Map<String, Object>> takeGroupTagMap(List<Group> groups) {


        Map<Tag, Map<String, Object>> alarmTagMap = new HashMap<>();
        for (Group group : groups) {
            List<Tag> alarmTags = new ArrayList<>();
            Map<String, Object> tagMap = new HashMap<>();
            for (Device device : group.getDeviceSet()) {
                for (Tag tag : device.getTags()) {
                    if(tag.getTagList().getIsAlarm()) {
                        alarmTags.add(tag);
                    }
                    if (actTripCode.contains(tag.getType())) {
                        Map<Integer, String> tripCodeMap = tag.getTagList().getTrips().stream()
                                .collect(toMap(t -> t.getCode(), k -> k.getMessage(), (k1, k2) -> k1));
                        tag.setValue(tripCodeMap.get(new Integer(tag.getValue().toString())));
                    }
                    tagMap.put(tag.getType(), tag.getValue());
                }
            }
            for (Tag alarmId : alarmTags) {
                alarmTagMap.put(alarmId, tagMap);
            }
        }
        return alarmTagMap;
    }

    @Scheduled(cron="*/1 * * * * *")
    public void alarmFixedRateTask() {


        List<Group> groups = groupDslRepository.findAllAllarmGroups(
                List.of(
                        "COMP_Trip", "COMP_Warning",
                        "PWR_KW",
                        "COMP_ActTripCode", "COMP_ActWarCode",
                        "COMP_TripCode", "COMP_WarningCode",
                        "COMP_ActTripCode"
                        ,"COMP_ActWarCode"
                        ,"COMP_SystemPre"
                        ,"COMP_AirDisTemp"
                )
        );
        Map<Tag, Map<String, Object>> alarmTagMap = takeGroupTagMap(groups);
        Set<Tag> tags = alarmTagMap.keySet();
        List<Alarm> newAlarms = new ArrayList<>();
        Set<Tag> takenAlarmTags = tags.stream()
                .filter(t -> new Integer(t.getValue().toString()) == 1)
                .collect(toSet());

        Set<Tag> newTags = new HashSet<>();
        newTags.addAll(takenAlarmTags);
        if(!isNull(savedTags)) newTags.removeIf(t->{
            return savedTags.stream()
                    .map(k -> k.getId())
                    .collect(toList())
                    .contains(t.getId());
        });


        for (Tag newTag : newTags) {
            Map<String, Object> insertAlarmValues = alarmTagMap.get(newTag);
            System.out.println(insertAlarmValues.toString());
            String message = null;
            for (String code : actTripCode) {

                if (!isNull(insertAlarmValues.get(code))) {
                    message = insertAlarmValues.get(code).toString();
                }
            }

            Optional<Object> comp_airDisTemp = Optional.ofNullable(insertAlarmValues.get("COMP_AirDisTemp"));
            Optional<Object> comp_systemPre = Optional.ofNullable(insertAlarmValues.get("COMP_SystemPre"));
            Optional<Object> pwr_kw = Optional.ofNullable(insertAlarmValues.get("PWR_KW").toString());
            newAlarms.add(Alarm.builder()
                            .kwhValue(new Double(pwr_kw.orElse((Object) new String("-110.0")).toString()))
                            .type(newTag.getType().equals("COMP_Trip")? "trip": "warning")
                            .checkIn(false)
                            .eventDate(LocalDate.now())
                            .message(message)

                            .tag(newTag)
                            .occurrenceTime(LocalTime.now())
                            .tempValue(new Double(comp_airDisTemp.orElse((Object) new String("-110.0")).toString()))
                            .prssValue(new Double(comp_systemPre.orElse((Object) new String("-110.0")).toString()))
                            .recoverDate(null)
                            .recoverTime(null)
                    .build());
        }

        if(!alarmInsert) alarmDataRepository.saveAll(newAlarms);

        if(isNull(savedTags)){
            savedTags = new HashSet<>(takenAlarmTags);
            alarmInsert = true;
        } else {
            savedTags = new HashSet<>();
            savedTags.addAll(takenAlarmTags);
        }

    }
    private static <T> ArrayList<T> difference(ArrayList<T> list1, ArrayList<T> list2) // 차집합
    {
        ArrayList<T> result = new ArrayList<>(100);
        result.addAll(list1);
        result.removeAll(list2);
        return result;
    }
    public Integer getWeekNumber (LocalDate date) {
        LocalDate firstMondayOfMonth = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));

        // 첫 월요일이면 바로 리턴
        if (firstMondayOfMonth.isEqual(date)) return 1;

        if (date.isAfter(firstMondayOfMonth)) {
            // 첫 월요일 이후일 때
            int diffFromFirstMonday = date.getDayOfMonth() - firstMondayOfMonth.getDayOfMonth();
            int weekNumber = (int) Math.ceil(diffFromFirstMonday / 7.0);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) weekNumber += 1;
            return weekNumber;
        }
        // 첫 월요일 이전이면 회귀식으로 전 달 마지막 주차를 구함
        return getWeekNumber(date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
    }

    public void crawl() {
        List<Device> devices =  deviceDslRepository.findAllDevices();
        for (Device device : devices) {
            System.out.println(device);
        }
    }
}
