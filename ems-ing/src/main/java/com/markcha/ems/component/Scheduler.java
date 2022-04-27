package com.markcha.ems.component;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.AlarmDataRepository;
import com.markcha.ems.repository.TripDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final TripDataRepository tripDataRepository;
    private final AlarmDataRepository alarmDataRepository;

    private static List<Tag> savedTags = null;
    private static Boolean alarmInsert = false;

    @Scheduled(fixedDelay = 1000)
    public void alarmFixedRateTask() {
        List<Tag> tags = deviceDslRepository.findAllAlarmTags();
        Map<Integer, Trip> tripMap = tripDataRepository.findAll().stream()
                .collect(toMap(Trip::getCode, trip->trip));
        List<Alarm> newAlarms = new ArrayList<>();
        List<Tag> takenAlarmTags = tags.stream()
                .filter(t -> t.getIsAlarm().equals(true))
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
                            ,"COMP_SumpPre"
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
                    String tripMesasge = tripMap.get(actTripCode).getMessage();
                    alarm.setTrip(tripMap.get(actTripCode));
                    alarm.setMessage(tripMesasge);
                    alarm.setType("trip");
                }
            }
            if(newTag.getType().equals("COMP_Warning")) {
                if (!isNull(alarmDataTagMap.get("COMP_ActWarCode"))) {
                    int actWarningCode = new Double(alarmDataTagMap.get("COMP_ActWarCode").getValue().toString()).intValue();
                    String tripMesasge = tripMap.get(actWarningCode).getMessage();
                    alarm.setTrip(tripMap.get(actWarningCode));
                    alarm.setMessage(tripMesasge);
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
}
