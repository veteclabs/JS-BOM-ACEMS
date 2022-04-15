package com.markcha.ems.mapper.alarm;

import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class AlarmMapDto {
    private List<String> tagNames;
}
