package com.markcha.ems.mapper.historyHour;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryHourSearchDto {
    private String tagType;
    private List<String> tagNames;
}
