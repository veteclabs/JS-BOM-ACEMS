package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDataRepository extends JpaRepository<Trip, Long> {
    Trip getOneByCode(Integer code);
}
