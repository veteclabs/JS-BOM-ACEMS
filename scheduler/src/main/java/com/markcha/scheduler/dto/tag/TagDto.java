package com.markcha.scheduler.dto.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.scheduler.domain.Tag;
import com.markcha.scheduler.dto.alarm.AlarmDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.Objects.isNull;

@Data
@Setter
@Getter
@NoArgsConstructor
public class TagDto {
    private Long id;
    private String tagName;
    private Object value;
    private String unit;
    private String description;
    private String type;
    @JsonIgnore
    private Long deviceId;
    @JsonIgnore
    private List<AlarmDto> alarms;
    public TagDto(Tag tag) {
        this.tagName = tag.getTagName();
        this.description = tag.getTagDescription();
        this.type = tag.getType();
        this.value = tag.getValue();
        this.unit = isNull(tag.getUnit())?"":tag.getUnit();
        if(!isNull(tag.getDevice())) this.deviceId = tag.getDevice().getId();
    }
    public TagDto(Tag tag, Boolean tagValue) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.description = tag.getTagDescription();
        this.value = tag.getValue();
        this.type = tag.getType();
        this.unit = isNull(tag.getUnit())?"":tag.getUnit();
        if(!isNull(tag.getDevice())) this.deviceId = tag.getDevice().getId();
    }
}

