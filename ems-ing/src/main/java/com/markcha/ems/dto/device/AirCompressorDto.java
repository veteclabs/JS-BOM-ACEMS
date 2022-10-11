package com.markcha.ems.dto.device;

import com.markcha.ems.domain.*;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.markcha.ems.dto.schedule.ScheduleDto;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;

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
    private List<TagDto> tags = new ArrayList<>();
    private Map<String, List<DeviceDto>> devices = new HashMap<>();
    private Map<String, List<TagDto>> tagByComponents;


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
            compGroup.getDeviceSet().forEach(t -> {
                if (!isNull(t.getEquipment())) {
                    if (t.getEquipment().getType().equals(EquipmentType.AIR_COMPRESSOR)) {
                        this.equipment = new EquipmentDto(t.getEquipment());
                        Map<String, List<TagDto>> groupingTags = new HashMap<>();

                        for (Tag tag : t.getTags().stream()
                                    .sorted(Comparator.comparing(q->q.getTagList().getId()))
                                    .collect(Collectors.toList())) {
                            for (TagSetMapper tagSetMapper : tag.getTagList().getTagSetMappers()) {
                                if(!groupingTags.containsKey(tagSetMapper.getTagSet().getNickname())) {
                                    groupingTags.put(tagSetMapper.getTagSet().getNickname(), new ArrayList<>());
                                }
                                groupingTags.get(tagSetMapper.getTagSet().getNickname())
                                        .add(TagDto.of(tag));
                            }
                        }

                        this.state = groupingTags.get("stateComponent").stream()
                                .collect(Collectors.toMap(k -> k.getType(), k -> k, (val1, val2) -> val1));
                        groupingTags.remove("stateComponent");
                        this.tagByComponents = groupingTags;

                    }
                }
            });
            if (!isNull(compGroup.getDeviceSet())) {
                this.devices = compGroup.getDeviceSet().stream()
                        .map(DeviceDto::new)
                        .filter(t -> !t.getType().equals(EquipmentType.AIR_COMPRESSOR))
                        .collect(groupingBy(t -> t.getType().getNickname()));
            }
            this.alarm = false;
            this.alarmMention = "테스트 메시지";
        }
    }
}