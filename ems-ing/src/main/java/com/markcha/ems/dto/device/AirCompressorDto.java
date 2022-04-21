package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.markcha.ems.dto.schedule.ScheduleDto;

import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class AirCompressorDto {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
    private Boolean alarm;
    private String alarmMention;
    private String unitId;
    private EquipmentDto equipment;
    private ScheduleDto schedule;
    @JsonIgnore
    private List<TagDto> tags;

    public AirCompressorDto(Device device) {
        if(!isNull(device)) {

            if (!isNull(device.getGroup())) {
                Group childGroup = device.getGroup();
                id = childGroup.getId();
                name = childGroup.getName();
                if (!isNull(childGroup.getParent())) {
                    groupId = childGroup.getParent().getId();
                    groupName = childGroup.getParent().getName();
                }
                if(!isNull(childGroup.getSchedule())) {
                    this.schedule = new ScheduleDto(childGroup.getSchedule());
                    this.schedule.setDayOfWeeks(childGroup.getSchedule().getDayOfWeekMappers().stream()
                            .map((dowmp) -> {
                                return new DayOfWeekDto(dowmp.getDayOfWeek());
                            })
                            .sorted(Comparator.comparing(DayOfWeekDto::getId))
                            .collect(toList()));
                }
            }
            if (!isNull(device.getEquipment())) {
                equipment = new EquipmentDto(device.getEquipment());
            }
            if (!isNull(device.getTags())) {
                tags = device.getTags().stream()
                        .map(TagDto::new)
                        .collect(toList());
            }
            this.alarm = false;
            this.alarmMention = "테스트 메시지";
            this.unitId = new String(String.format("U%03d", device.getId().intValue()));
        }
    }
}