package com.markcha.ems.dto.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.domain.TagSetMapper;
import com.markcha.ems.dto.tag.TagDto;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Builder
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConDto {
    private Long id;
    private String name;
    private EquipmentType equipmentType;
    private Map<String, TagDto> tags;
    private Map<String, List<TagDto>> tagByComponents;
    private Map<String, TagDto> state;
    public DeviceConDto(Device device) {
        id = device.getId();
        name = device.getName();
        if (!isNull(device.getTags())) {
            this.tags = device.getTags().stream()
                    .map(t->new TagDto(t, true))
                    .collect(toMap(TagDto::getType, t->t, (v1, v2) -> v1));
        }
        if (!isNull(device.getEquipment())) {
            this.equipmentType = device.getEquipment().getType();
        }
    }
    public static DeviceConDto of(Device device) {
        DeviceConDto build = DeviceConDto.builder()
                .id(device.getId())
                .name(device.getName())
                .equipmentType(!isNull(device.getEquipment()) ? device.getEquipment().getType() : null)
                .build();
        Map<String, List<TagDto>> tagByComponents = new HashMap<>();
        for (Tag tag : device.getTags().stream()
            .sorted(Comparator.comparing(t->t.getTagList().getId()))
            .collect(Collectors.toList())) {
            for (TagSetMapper tagSetMapper : tag.getTagList().getTagSetMappers()) {
                if (isNull(tagByComponents.get(tagSetMapper.getTagSet().getNickname()))) {
                    tagByComponents.put(tagSetMapper.getTagSet().getNickname(), new ArrayList<>());
                }
                tagByComponents.get(tagSetMapper.getTagSet().getNickname())
                        .add(TagDto.of(tag));
            }
        }
        if (!isNull(tagByComponents.get("stateComponent"))) {
            build.setState(tagByComponents.get("stateComponent").stream()
                    .collect(Collectors.toMap(k -> k.getType(), k -> k, (val1, val2) -> val1)));
            tagByComponents.remove("stateComponent");
        }
        build.setTagByComponents(tagByComponents);
        return build;
    }
}
