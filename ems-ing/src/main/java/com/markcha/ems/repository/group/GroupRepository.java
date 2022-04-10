package com.markcha.ems.repository.group;


import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;

import java.util.List;

public interface GroupRepository {
    public List<Group> findAllByType(String type);
    public Group getOneById(Long id);
    public Group getOneByDeviceId(Long id);
    public List<Group> findAllJoinSchedule();
    public List<Group> findAllByIds(List<Long> ids);
    public List<Group> findAllChildGroupsById(Long id);
}
