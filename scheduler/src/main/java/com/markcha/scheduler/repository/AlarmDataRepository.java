package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmDataRepository extends JpaRepository<Alarm, Long> {
}
