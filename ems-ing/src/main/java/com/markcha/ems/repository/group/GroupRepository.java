package com.markcha.ems.repository.group;


import com.markcha.ems.domain.Group;

import java.util.List;

public interface GroupRepository {
    public List<Group> findAllByType(String type);
    public Group getOneById(Long id);
    public Group getOneByDeviceId(Long id);
}
