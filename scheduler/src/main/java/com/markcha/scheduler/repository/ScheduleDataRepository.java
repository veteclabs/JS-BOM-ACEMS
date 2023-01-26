package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleDataRepository extends JpaRepository<Schedule, Long> {
}
