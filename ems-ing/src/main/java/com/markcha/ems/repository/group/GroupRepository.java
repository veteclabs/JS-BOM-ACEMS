package com.markcha.ems.repository.group;


import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.Link;

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
