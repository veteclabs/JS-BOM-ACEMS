package com.markcha.ems.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface HistoryHourMapper {
    List<HashMap<String, Object>> getHistoryHour(HashMap<String, Object> params);
}
