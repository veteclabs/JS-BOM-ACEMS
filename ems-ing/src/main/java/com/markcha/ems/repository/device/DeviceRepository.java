package com.markcha.ems.repository.device;

import com.markcha.ems.domain.Device;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;

public interface DeviceRepository {
    public List<Device> findAllTemplcates(String typeName);
    public List<Device> findAllCompressors(String typeName);
}
