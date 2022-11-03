package com.markcha.ems.repository;

import com.markcha.ems.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TripDataRepository extends JpaRepository<Trip, Long> {
    Trip getOneByCode(Integer code);
}
