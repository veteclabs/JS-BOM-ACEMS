package com.markcha.ems.repository.device;

import com.markcha.ems.domain.Device;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;

public interface DeviceRepository {
    List<Device> findAllTemplcates(String typeName);
    List<Device> findAllCompressors(String typeName);
    Device getOneById(Long id);
    Device getOneByIdJoinGroupSchedule(Long id);
    List<Device> findAllDeviceGroupByTagSet();
}
