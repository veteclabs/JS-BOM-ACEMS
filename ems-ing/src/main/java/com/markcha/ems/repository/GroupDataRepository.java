package com.markcha.ems.repository;

import com.markcha.ems.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupDataRepository extends JpaRepository<Group, Long> {
}
