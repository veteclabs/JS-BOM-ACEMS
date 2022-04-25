package com.markcha.ems.repository;

import com.markcha.ems.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDataRepository extends JpaRepository<Trip, Long> {
}
