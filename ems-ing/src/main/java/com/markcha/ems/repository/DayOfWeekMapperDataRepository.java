package com.markcha.ems.repository;

import com.markcha.ems.domain.DayOfWeekMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DayOfWeekMapperDataRepository extends JpaRepository<DayOfWeekMapper, Long> {
    @Modifying
//    @Query("delete from DayOfWeekMapper dowm where dowm.schedule.id = ?1")
    public void deleteAllById(Long id);
}
