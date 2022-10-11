package com.markcha.ems.repository.tag.intercept.dto;

import lombok.Data;

@Data
public class TagListDto {
    protected Long id;
    protected String tagDescription;
    protected String unit;
    protected Boolean unitConversion;
    protected Boolean isUsage;
    protected Boolean isTrend;
    protected Integer loggingTime;
    protected String nickname;
    protected Boolean isAlarm;
    protected Boolean showAble;
    protected String type;
    protected Double min;
    protected Double max;
    protected String testType;
}