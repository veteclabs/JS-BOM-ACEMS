package com.markcha.ems.mapper;

import com.markcha.ems.mapper.analysis.HistorySearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface HistoryMapper {
    Double getHistory(HistorySearchDto historySearchDto);

}

