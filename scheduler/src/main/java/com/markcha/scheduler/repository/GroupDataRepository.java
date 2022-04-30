package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupDataRepository extends JpaRepository<Group, Long> {
}
