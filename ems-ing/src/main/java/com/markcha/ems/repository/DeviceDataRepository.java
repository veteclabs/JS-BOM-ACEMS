package com.markcha.ems.repository;


import com.markcha.ems.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceDataRepository extends JpaRepository<Device, Long> {

}
