package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.order.OrderDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.week.WeekDto;
import com.markcha.ems.repository.DayOfWeekDataRepository;
import com.markcha.ems.repository.group.dto.DeviceQueryDto;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;
    private final ScheduleDslRepositoryImpl scheduleDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final OrderDslRepositoryImpl orderDslRepository;
    private final DayOfWeekDataRepository dayOfWeekDataRepository;

    @GetMapping(value="/schedules")
    public List<ScheduleSimpleDto> schedules(
    ) {

        return scheduleDslRepository.getAll().stream()
                .map(ScheduleSimpleDto::new)
                .collect(toList());
    }
    @GetMapping(value="/schedule/{scheduleId}")
    public List<ScheduleDto> schedule(
            @PathVariable("scheduleId") Long scheduleId
    ) {
        return groupDslRepository.findAllJoinScheduleByScheduleId(scheduleId).stream()
                .map(ScheduleDto::new)
                .collect(toList());
    }
    @GetMapping(value="/dayOfWeek")
    public List<DayOfWeekDto> dayOfWeek() {
        return dayOfWeekDataRepository.findAll().stream()
                .map(DayOfWeekDto::new)
                .collect(toList());
    }
    @GetMapping(value="/schedule/{scheduleId}/week/{weekId}")
    public List<OrderDto> weeks(
            @PathVariable("scheduleId") Long scheduleId,
            @PathVariable("weekId") Long weekId
    ) {
        Long rootGroupId = scheduleDslRepository.findRootGroupId(scheduleId).getId();
        return orderDslRepository.findAllByRootGroupIdWeekId(rootGroupId, weekId).stream()
                .map(OrderDto::new)
                .collect(toList());
    }
    @GetMapping(value="/devices/{groupId}")
    public List<DeviceQueryDto> weeks(
            @PathVariable("groupId") Long groupId,
            @RequestBody GroupSearchDto groupSearchDto
    ) {
        List<Long> rootId = new ArrayList<>();
        rootId.add(groupId);
        groupSearchDto.setTagInTypes(QTag.tag.type.in(groupSearchDto.getTagTypes()));
        List<DeviceQueryDto> devices = new ArrayList<>();
        groupDynamicRepository.getAnalysisLocations(rootId, groupSearchDto, true).stream()
                .map(t -> new GroupQueryDto(t, false))
                .forEach(k->devices.addAll(k.getAllDevices()));
        return devices;
    }
    @Data
    @NoArgsConstructor
    public static class ScheduleSimpleDto {
        private Long id;
        private Integer interval;
        private Boolean isGroup;
        private Boolean isActive;
        private LocalDateTime updated;
        public ScheduleSimpleDto(Schedule schedule) {
            this.id = schedule.getId();
            this.interval = schedule.getInterval();
            this.isGroup = schedule.getIsGroup();
            this.updated = schedule.getUpdated();
            this.isActive = schedule.getIsActive();
        }
    }
    @Data
    @NoArgsConstructor
    public static class ScheduleDto {

        private Long scheduleId;
        private Long groupId;
        private Integer interval;
        private Boolean isGroup;
        private Boolean isActive;
        @JsonIgnore
        private String type;
        private LocalTime startTime;
        private LocalTime stopTime;
        private Double min;
        private Double max;
        @JsonIgnore
        private LocalDateTime updated;
        private List<Integer> dayOfWeeks;
        private List<Integer> weeks;
//        private List<TagDto> tags = new ArrayList<>();
        private Double pressure;
        public ScheduleDto(Group group) {
            if(!isNull(group.getDeviceSet())) {
                this.groupId = group.getId();
            }
            if(!isNull(group.getSchedule())) {
                Schedule schedule = group.getSchedule();
                this.scheduleId = schedule.getId();
                this.interval = schedule.getInterval();
                this.isGroup = schedule.getIsGroup();
                this.type = schedule.getType();
                this.startTime = schedule.getStartTime();
                this.stopTime = schedule.getStopTime();
                this.updated = schedule.getUpdated();
                this.min = schedule.getMin();
                this.max = schedule.getMax();
                this.isActive = schedule.getIsActive();
                if(!isNull(schedule.getWeeks())) {
                    this.weeks = schedule.getWeekMappers().stream()
                            .map(t->t.getWeek().getIdx())
                            .sorted(comparing(k->k))
                            .collect(toList());
                }
                if(!isNull(schedule.getDayOfWeekMappers())) {
                    this.dayOfWeeks = schedule.getDayOfWeekMappers().stream()
                            .map(t->t.getDayOfWeek().getIdx())
                            .collect(toList());
                    this.dayOfWeeks = new ArrayList<>(new HashSet<>(this.dayOfWeeks));
                }

            }
            if(!isNull(group.getTagetDevice())) {
                if(!isNull(group.getTagetDevice().getPressure())) {
                    this.pressure = group.getTagetDevice().getPressure().getValue();
                }
            }
        }
    }

}
