package com.markcha.ems.mapper.updateSql;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UpdateMapper {
    void updateStateComponent(UpdateQueryDto updateQueryDto);
    void updateMainPageComponent(UpdateQueryDto updateQueryDto);
    void updateDetailPageComponent(UpdateQueryDto updateQueryDto);
    void updateImportantComponent(UpdateQueryDto updateQueryDto);
    void updateGroupPageComponent(UpdateQueryDto updateQueryDto);
    void deleteTagSetMapperDuplicate(UpdateQueryDto updateQueryDto);
    void deleteSroucesHourWithDay(UpdateQueryDto updateQueryDto);
    void insertSroucesHourWithDay(UpdateQueryDto updateQueryDto);
    void deleteSourcesFiveMinute(UpdateQueryDto updateQueryDto);
    void insertSourcesFiveMinute(UpdateQueryDto updateQueryDto);

}
