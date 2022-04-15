package com.markcha.ems.dto.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.dto.alarm.AlarmDto;
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
    private String tagName;
    private Double value;
    private String unit;
    private String description;
    @JsonIgnore
    private Long deviceId;
    @JsonIgnore
    private List<AlarmDto> alarms;
    public TagDto(Tag tag) {
        this.tagName = tag.getTagName();
        this.description = tag.getTagDescription();
        this.unit = isNull(tag.getUnit())?"":tag.getUnit();
        if(!isNull(tag.getDevice())) this.deviceId = tag.getDevice().getId();
    }
}

