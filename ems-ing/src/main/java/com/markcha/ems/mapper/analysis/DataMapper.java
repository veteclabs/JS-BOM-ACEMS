package com.markcha.ems.mapper.analysis;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface DataMapper {
    List<HashMap<String, Object>> getHistoryHour(HistorySearchDto historySearchDto);
    List<HashMap<String, Object>> getHistoryDay(HistorySearchDto historySearchDto);
    List<HashMap<String, Object>> getHistoryMin(HistorySearchDto historySearchDto);
    List<HashMap<String, Object>> getHistoryMonth(HistorySearchDto historySearchDto);
    List<HashMap<String, Object>> getHistoryYear(HistorySearchDto historySearchDto);

}

