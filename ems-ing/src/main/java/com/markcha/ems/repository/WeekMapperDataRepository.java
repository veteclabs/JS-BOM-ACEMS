package com.markcha.ems.repository;

import com.markcha.ems.domain.WeekMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WeekMapperDataRepository extends JpaRepository<WeekMapper, Long> {
    @Modifying
    @Query("delete from WeekMapper dowm where dowm.schedule.id = ?1")
    public long deleteAllBySchedule_Id(Long id);
}
