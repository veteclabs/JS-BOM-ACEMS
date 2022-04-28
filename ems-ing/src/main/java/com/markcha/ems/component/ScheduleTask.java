package com.markcha.ems.component;

import com.markcha.ems.controller.ScheduleController;
import com.markcha.ems.controller.ScheduleController.ScheduleDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimerTask;

import static java.util.stream.Collectors.toList;


public class ScheduleTask extends TimerTask {
    private final GroupDslRepositoryImpl groupDslRepository;

    private Long id;

    public ScheduleTask(GroupDslRepositoryImpl groupDslRepository) {
        this.groupDslRepository = groupDslRepository;
    }

    @Override
    public void run() {
//        System.out.println(this.id  + " 번 스레드");
//        List<ScheduleDto> schedules = groupDslRepository.findAllJoinScheduleByScheduleId(this.id).stream()
//                .map(ScheduleDto::new)
//                .collect(toList());
//        schedules.forEach(t-> {
//            System.out.println(t.getGroupId());
//        });
    }
    public void setScheduleId(Long id) {
        this.id = id;
    }
}
