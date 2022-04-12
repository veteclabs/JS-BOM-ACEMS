package com.markcha.ems.mapper.analysis;

import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Setter
public class HistorySearchDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String date;
    private List<String> tagNames = new ArrayList<>();
    private List<String> secondTagNames = new ArrayList<>();
    private String timeType;
    private String usageType;

    private String beforeOneDay;
    private String beforeOneMonth;
    private String beforeOneYear;
    private Long energyId;
    private Boolean isDuo;

    public HistorySearchDto(LocalDate startDate, LocalDate endDate, String date, List<String> tagNames, String timeType, String usageType, String beforeOneDay, String beforeOneMonth, String beforeOneYear, Long energyId)   {
        this.startDate = startDate;
        this.endDate = endDate;
        this.date = date;
        this.tagNames = tagNames;
        this.timeType = timeType;
        this.usageType = usageType;
        this.beforeOneDay = "beforeDay" + usageType;
        this.beforeOneMonth = "beforeOneMonth" + usageType;
        this.beforeOneYear = "beforeOneYear" + usageType;
        this.energyId = energyId;
    }
}

