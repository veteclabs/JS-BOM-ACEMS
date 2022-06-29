package com.markcha.ems.mapper.historyHour;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface HistoryHourMapper {
    double getHistoryHour(String tagType);
}
