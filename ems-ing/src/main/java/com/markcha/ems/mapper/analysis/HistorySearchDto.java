package com.markcha.ems.mapper.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startOneDayBeforeDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endOneDayBeforeDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startOneMomthBeforeDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endOneMomthBeforeDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startOneYearBeforeDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endOneYearBeforeDate;

    private String tagType;
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

    public HistorySearchDto(LocalDate startDate, LocalDate endDate, String date, List<String> tagNames, String timeType, String usageType, String beforeOneDay, String beforeOneMonth, String beforeOneYear, Long energyId) throws ParseException {

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
        Object obj = new JSONParser().parse(date);
        JSONObject jsonObj = (JSONObject) obj;
        String start = jsonObj.get("start").toString();
        String end =  jsonObj.get("end").toString();
        this.startDate = convertStringToDate(start);
        this.endDate = convertStringToDate(end);
        this.startOneDayBeforeDate = this.startDate.plusDays(-1);
        this.endOneDayBeforeDate = this.endDate.plusDays(-1);
        this.startOneMomthBeforeDate = this.startDate.plusMonths(-1);
        this.endOneMomthBeforeDate = this.endDate.plusMonths(-1);
        this.startOneYearBeforeDate = this.startDate.plusYears(-1);
        this.endOneYearBeforeDate = this.endDate.plusYears(-1);
    }

    private LocalDate convertStringToDate(String date) {
        if(date.length() > 10) {
            return LocalDate.parse(date.substring(0, 10));
        } else if(date.length() == 10) {
            return LocalDate.parse(date);
        } else if(date.length() == 7) {
            return LocalDate.parse(date + "-01");
        } else if(date.length() == 4) {
            return LocalDate.parse(date + "-01-01");
        }
        return null;
    }
}



