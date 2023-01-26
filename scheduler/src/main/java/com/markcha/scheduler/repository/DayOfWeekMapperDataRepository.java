package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.DayOfWeekMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface DayOfWeekMapperDataRepository extends JpaRepository<DayOfWeekMapper, Long> {
    @Modifying
//    @Query("delete from DayOfWeekMapper dowm where dowm.schedule.id = ?1")
    public void deleteAllById(Long id);
}
