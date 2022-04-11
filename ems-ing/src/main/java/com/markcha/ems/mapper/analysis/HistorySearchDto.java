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
    private String timeType;
    private String usageType;

    private String beforeOneDay;
    private String beforeOneMonth;
    private String beforeOneYear;
    private Long energyId;

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

//        this.tagNames = Arrays.asList("U150_PWR_KWh","U151_PWR_KWh","U152_PWR_KWh","U153_PWR_KWh","U154_PWR_KWh","U155_PWR_KWh","U156_PWR_KWh","U157_PWR_KWh","U158_PWR_KWh","U159_PWR_KWh","U160_PWR_KWh","U161_PWR_KWh","U162_PWR_KWh","U163_PWR_KWh","U164_PWR_KWh","U165_PWR_KWh","U166_PWR_KWh","U167_PWR_KWh","U168_PWR_KWh","U169_PWR_KWh","U170_PWR_KWh","U171_PWR_KWh","U172_PWR_K Wh","U173_PWR_KWh","U174_PWR_KWh","U175_PWR_KWh","U176_PWR_KWh","U177_PWR_KWh","U178_PWR_KWh","U179_PWR_KWh","U180_PWR_KWh","U181_PWR_KWh","U182_PWR_KWh","U183_PWR_KWh","U184_PWR_KWh","U185_PWR_KWh","U186_PWR_KWh","U187_PWR_KWh","U188_PWR_KWh","U189_PWR_KWh","U190_PWR_KWh","U191_PWR_KWh","U192_PWR_KWh","U193_PWR_KWh","U194_PWR_KWh","U195_PWR_KWh","U196_PWR_K Wh","U197_PWR_KWh","U198_PWR_KWh","U199_PWR_KWh","U200_PWR_KWh","U201_PWR_KWh","U202_PWR_KWh","U203_PWR_KWh","U204_PWR_KWh","U205_PWR_KWh","U206_PWR_KWh","U207_PWR_KWh","U208_PWR_KWh","U209_PWR_KWh","U210_PWR_KWh","U211_PWR_KWh","U212_PWR_KWh","U213_PWR_KWh","U214_PWR_KWh");
    }
}

