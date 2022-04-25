package com.markcha.ems.component;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.markcha.ems.domain.QTag.tag;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    @Scheduled(fixedDelay = 1000)
    public void alarmFixedRateTask() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();




        List<Tag> tags = deviceDslRepository.findAllAlarmTags();
        List<Alarm> newAlarms = new ArrayList<>();
        Map<String, Tag> takenAlarmTags = tags.stream()
                .filter(t -> t.getIsAlarm().equals(true))
                .filter(t -> t.getValue().equals(1))
                .collect(toMap(Tag::getType, tag->tag));
        if (!isNull(takenAlarmTags.get("COMP_Trip"))) {
            List<Tag> tripCodeTag = takenAlarmTags.get("COMP_Trip").getDevice().getTags().stream()
                    .filter(t -> t.getType().equals("COMP_ActTripCode"))
                    .collect(toList());
            if (tripCodeTag.size() == 1) {
                tripCodeTag.get(0).getValue();
            }
        }
//        takenAlarmTags.forEach(t->{
//            if (t.getType().equals("COMP_Trip")) {
//                t.getDevice().getTags().forEach(k -> {
//                    k.getType("");
//                });
//            }
//        });
        stopWatch.stop();
//        System.out.println("해당 노드 그룹의 장치들 조회 : " + stopWatch.getLastTaskTimeMillis());
    }
}
