package com.markcha.scheduler.repository.device;

import com.markcha.scheduler.domain.Device;

import java.util.List;

public interface DeviceRepository {
    public List<Device> findAllTemplcates(String typeName);
    public List<Device> findAllCompressors(String typeName);
    public Device getOneById(Long id);
    public Device getOneByIdJoinGroupSchedule(Long id);
}
