package com.markcha.scheduler.repository;


import com.markcha.scheduler.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceDataRepository extends JpaRepository<Device, Long> {

}
