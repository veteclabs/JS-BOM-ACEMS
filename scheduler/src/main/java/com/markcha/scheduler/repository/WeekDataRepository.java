package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeekDataRepository extends JpaRepository<Week, Long> {
    @Query("select w from Week w where w.id in (?1)")
    public List<Week> findAllByIdIn(List<Long> ids);
}
