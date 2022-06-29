package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.Group;
import com.markcha.ems.dto.dayofweek.DayOfWeekDto;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.markcha.ems.dto.schedule.ScheduleDto;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.Pair.toMap;

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
    private Map<String, TagDto> state = new HashMap<>();
    private Map<String, TagDto> tags = new HashMap<>();
    private Map<String, List<DeviceDto>> devices = new HashMap<>();
    public AirCompressorDto(Group compGroup) {
        if (!isNull(compGroup)) {
            id = compGroup.getId();
            name = compGroup.getName();
            if (!isNull(compGroup.getParent())) {
                groupId = compGroup.getParent().getId();
                groupName = compGroup.getParent().getName();
            }
            if (!isNull(compGroup.getSchedule())) {
                this.schedule = new ScheduleDto(compGroup.getSchedule());
            }
            compGroup.getDeviceSet().forEach(t->{
                if(!isNull(t.getEquipment())) {
                    if(t.getEquipment().getType().equals(EquipmentType.AIR_COMPRESSOR)) {
                        this.equipment = new EquipmentDto(t.getEquipment());
                        List<TagDto> collect = t.getTags().stream()
                                .map(k -> new TagDto(k, true))
                                .collect(toList());
                        this.state = collect.stream()
                                .filter(k -> {
                                    if (new ArrayList<String>(List.of(
                                            "COMP_Power"
                                            ,"COMP_Local"
                                            ,"COMP_ActTripCode"
                                            ,"COMP_Trip"
                                            ,"COMP_Load"
                                            ,"COMP_AutoStop"
                                            ,"COMP_Warning"
                                            ,"COMP_ActWarCode"
                                    )).contains(k.getType())) {
                                        k.setValue(new Double(k.getValue().toString()).intValue());
                                    }
                                    return new ArrayList<String>(List.of(
                                            "COMP_Power"
                                            ,"COMP_StartPre"
                                            ,"COMP_StopPre"
                                            ,"COMP_Local"
                                            ,"COMP_ActTripCode"
                                            ,"COMP_Trip"
                                            ,"COMP_AutoStop"
                                            ,"COMP_Load"
                                            ,"COMP_Warning"
                                            ,"COMP_ActWarCode"
                                    )).contains(k.getType());
                                })
                                .collect(Collectors.toMap(TagDto::getType, tagDto -> tagDto));
                        this.tags = collect.stream()
                                .collect(Collectors.toMap(TagDto::getType, tagDto -> tagDto));
                    }
                }
            });
            if (!isNull(compGroup.getDeviceSet())) {
                this.devices = compGroup.getDeviceSet().stream()
                        .map(DeviceDto::new)
                        .filter(t->!t.getType().equals(EquipmentType.AIR_COMPRESSOR))
                        .collect(groupingBy(t->t.getType().getNickname()));
            }
            this.alarm = false;
            this.alarmMention = "테스트 메시지";
        }
    }
}