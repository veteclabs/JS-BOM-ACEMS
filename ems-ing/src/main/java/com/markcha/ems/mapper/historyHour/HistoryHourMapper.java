package com.markcha.ems.mapper.historyHour;

import com.markcha.ems.mapper.analysis.HistorySearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface HistoryHourMapper {
    Double getHistoryHour(HistoryHourSearchDto historySearchDto);
}
