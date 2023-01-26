package com.markcha.ems.repository;

import com.markcha.ems.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleDataRepository extends JpaRepository<Schedule, Long> {
}
