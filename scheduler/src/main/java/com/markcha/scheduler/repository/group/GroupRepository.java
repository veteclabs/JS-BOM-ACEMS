package com.markcha.scheduler.repository.group;


import com.markcha.scheduler.domain.Group;
import com.markcha.scheduler.domain.Link;
import com.markcha.scheduler.dto.group.GroupSearchDto;

import java.util.List;

public interface GroupRepository {
    public List<Group> findAllByType(String type);
    public Group getOneById(Long id);
    public Group getOneByDeviceId(Long id);
    public List<Group> findAllJoinSchedule();
    public List<Group> findAllByIds(List<Long> ids);
    public List<Group> findAllChildGroupsById(Long id);
    public Group getOneJoinSchedule(Long id);
    List<Group> getAnalysisLocations(List<Long> locaionIds, GroupSearchDto locationSearchDto, Boolean deep);
    List<Link> getDynamicLocations(List<Long> locaionIds, GroupSearchDto locationSearchDto, Boolean deep);
    List<Long> getLevelIds(Integer level);
    List<Long> getTypeIds(String type);
}
