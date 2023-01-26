package com.markcha.ems.mapper.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@AllArgsConstructor
public class AlarmMapDto {
    private List<String> tagNames;
}
