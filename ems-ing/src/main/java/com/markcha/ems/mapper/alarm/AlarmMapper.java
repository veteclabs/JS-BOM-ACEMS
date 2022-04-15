package com.markcha.ems.mapper.alarm;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface AlarmMapper {
    List<HashMap<String, Object>> getTodayAlarmState(AlarmMapDto alarmDto);
}
