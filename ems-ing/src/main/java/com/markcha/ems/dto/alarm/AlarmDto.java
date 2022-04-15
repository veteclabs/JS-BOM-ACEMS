package com.markcha.ems.dto.alarm;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;

@Data
@Getter
public class AlarmDto {
    private String tagName;
    private Boolean checkIn;
    private Integer usage;
    private Integer lastUsage;
    private Integer taget;
    private String description;
    public AlarmDto(HashMap<String, Object> object) {
        tagName = (String) object.get("tagName");
        checkIn = (Boolean) object.get("checkIn");
        usage = (Integer) object.get("usage");
        lastUsage = (Integer) object.get("lastUsage");
        taget = (Integer) object.get("taget");
        description = (String) object.get("description");

    }
}