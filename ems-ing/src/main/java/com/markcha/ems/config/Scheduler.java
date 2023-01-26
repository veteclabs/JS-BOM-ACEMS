package com.markcha.ems.config;



import com.markcha.ems.controller.ScheduleController;
import com.markcha.ems.domain.Alarm;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.domain.Trip;
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


    private static List<Tag> savedTags = null;
    private static Boolean alarmInsert = false;

    private static Map<Long, Timer> tasks = new HashMap<>();


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

    public void alarmFixedRateTask() {
        List<Tag> tags = deviceDslRepository.findAllAlarmTags();
        Map<Integer, List<Trip>> tripMap = tripDataRepository.findAll().stream()
                .collect(groupingBy(Trip::getCode, toList()));
        List<Alarm> newAlarms = new ArrayList<>();
        List<Tag> takenAlarmTags = tags.stream()
                .filter(t -> t.getTagList().equals(true))
                .filter(t -> new Double(t.getValue().toString()).intValue() == 1)
                .collect(toList());

        List<Tag> newTags = new ArrayList<>();
        newTags.addAll(takenAlarmTags);
        if(!isNull(savedTags)) newTags.removeIf(t->{
            return savedTags.stream()
                    .map(k -> k.getId())
                    .collect(toList())
                    .contains(t.getId());
        });


        for (Tag newTag : newTags) {
            Alarm alarm = new Alarm();
            Map<String, Tag> alarmDataTagMap = newTag.getDevice().getTags().stream()
                    .filter(t -> new ArrayList<>(List.of(
                            "COMP_ActTripCode"
                            ,"COMP_ActWarCode"
                            ,"COMP_SystemPre"
                            ,"COMP_AirDisTemp"
                    )).contains(t.getType()))
                    .collect(toMap(Tag::getType, tag->tag));
            if (!isNull(newTag.getDevice().getGroup().getDevices())) {
                Map<String, List<Tag>> powerTagMap = new ArrayList<>(newTag.getDevice().getGroup().getDevices()).stream()
                        .map(t->{
                            return t.getTags().stream()
                                    .filter(k->k.getType().equals("PWR_KWh"))
                                    .collect(toList());
                        })
                        .flatMap(List::stream)
                        .collect(groupingBy(t->t.getType()));
                if(!isNull(powerTagMap.get("PWR_KWh"))) {
                    double pwr_kWhSum = powerTagMap.get("PWR_KWh").stream().mapToDouble(i -> new Double(i.getValue().toString())).sum();
                    if(!isNull(powerTagMap.get("PWR_KWh"))) {
                        Tag pwr_kWh = powerTagMap.get("PWR_KWh").get(0);
                        pwr_kWh.setValue(pwr_kWhSum);
                        alarmDataTagMap.put("PWR_KWh", pwr_kWh);
                    }
                }
            } else {
                alarmDataTagMap.put("PWR_KWh", null);
            }

            if(newTag.getType().equals("COMP_Trip")) {
                if (!isNull(alarmDataTagMap.get("COMP_ActTripCode"))) {
                    int actTripCode = new Double(alarmDataTagMap.get("COMP_ActTripCode").getValue().toString()).intValue();
                    Trip trip = tripMap.get(actTripCode).stream().filter(t -> t.getEquipment().getId() == newTag.getDevice().getEquipment().getId()).findFirst().get();
                    alarm.setTrip(trip);
                    alarm.setMessage(trip.getMessage());
                    alarm.setType("trip");
                }
            }
            if(newTag.getType().equals("COMP_Warning")) {
                if (!isNull(alarmDataTagMap.get("COMP_ActWarCode"))) {
                    int actWarningCode = new Double(alarmDataTagMap.get("COMP_ActWarCode").getValue().toString()).intValue();
                    Trip trip = tripMap.get(actWarningCode).stream().filter(t -> t.getEquipment().getId() == newTag.getDevice().getEquipment().getId()).findFirst().get();
                    alarm.setTrip(trip);
                    alarm.setMessage(trip.getMessage());
                    alarm.setType("warning");
                }
            }
            if (!isNull(alarmDataTagMap.get("PWR_KWh"))) {
                Double kwhValue = new Double(alarmDataTagMap.get("PWR_KWh").getValue().toString());
                alarm.setKwhValue(kwhValue);
            }
            if (!isNull(alarmDataTagMap.get("COMP_SumpPre"))) {
                Double kwhValue = new Double(alarmDataTagMap.get("COMP_SumpPre").getValue().toString());
                alarm.setPrssValue(kwhValue);
            }
            if (!isNull(alarmDataTagMap.get("COMP_AirDisTemp"))) {
                Double kwhValue = new Double(alarmDataTagMap.get("COMP_AirDisTemp").getValue().toString());
                alarm.setTempValue(kwhValue);
            }
            alarm.setEventDate(LocalDate.now());
            alarm.setCheckIn(false);
            alarm.setOccurrenceTime(LocalTime.now());
            alarm.setTag(newTag);
            newAlarms.add(alarm);
        }

        if(alarmInsert) alarmDataRepository.saveAll(newAlarms);

        if(isNull(savedTags)){
            savedTags = new ArrayList<>(takenAlarmTags);
            alarmInsert = true;
        } else {
            savedTags = new ArrayList<>();
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
