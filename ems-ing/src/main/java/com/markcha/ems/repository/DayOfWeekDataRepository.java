package com.markcha.ems.repository;

import com.markcha.ems.domain.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DayOfWeekDataRepository extends JpaRepository<DayOfWeek, Long> {
    @Query("select dofw from DayOfWeek dofw where dofw.id in (?1)")
    public List<DayOfWeek> findAllByIdIn(List<Long> ids);
}
