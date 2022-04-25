package com.markcha.ems.repository;

import com.markcha.ems.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmDataRepository extends JpaRepository<Alarm, Long> {
}
