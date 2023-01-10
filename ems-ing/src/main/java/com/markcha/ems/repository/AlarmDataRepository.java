package com.markcha.ems.repository;

import com.markcha.ems.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmDataRepository extends JpaRepository<Alarm, Long> {

    @Query(value = "" +
            "select a " +
            "from Alarm a " +
            "left join fetch a.tag t " +
            "left join fetch t.device d " +
            "where d.id = :deviceId")
    List<Alarm> findAllByDeviceId(@Param("deviceId") Long id);
}
