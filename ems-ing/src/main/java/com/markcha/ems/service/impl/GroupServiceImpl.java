package com.markcha.ems.service.impl;

import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.repository.DayOfWeekDataRepository;
import com.markcha.ems.repository.GroupDataRepository;
import com.markcha.ems.repository.ScheduleDataRepository;
import com.markcha.ems.repository.WeekDataRepository;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl {
    private final GroupDslRepositoryImpl groupDslRepository;
    private final GroupDataRepository groupDataRepository;
    private final DayOfWeekDataRepository dayOfWeekDataRepository;
    private final WeekDataRepository weekDataRepository;
    private final ScheduleDataRepository scheduleDataRepository;

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
        List<DayOfWeek> dayOfWeeks = dayOfWeekDataRepository.findAllByIdIn(Arrays.asList(new Long[]{1L, 2L, 3L, 4L}));
        System.out.println(dayOfWeeks);
        List<DayOfWeekMapper> newDayOfWeekMappers = new ArrayList<>();
        for (DayOfWeek dayOfWeek: dayOfWeeks) {
            DayOfWeekMapper dayOfWeekMapper = new DayOfWeekMapper();
            dayOfWeekMapper.setDayOfWeek(dayOfWeek);
            dayOfWeekMapper.setSchedule(newSchedule);
            newDayOfWeekMappers.add(dayOfWeekMapper);
        }
        // 요일 끝

        // 주차 관계 생성
        List<Long> weekIds = scheduleDto.getWeeks().stream()
                .map(WeekDto::getId)
                .collect(toList());
        List<Week> weeks = weekDataRepository.findAllByIdIn(weekIds);
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
}
