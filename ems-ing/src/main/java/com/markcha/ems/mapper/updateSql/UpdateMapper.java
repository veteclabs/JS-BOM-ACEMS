package com.markcha.ems.mapper.updateSql;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UpdateMapper {
    void updateStateComponent();
    void updateMainPageComponent();
    void updateDetailPageComponent();
    void updateImportantComponent();
    void updateGroupPageComponent();
    void deleteTagSetMapperDuplicate();
    void deleteSroucesHourWithDay();
    void insertSroucesHourWithDay();

}
