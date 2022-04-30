package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDataRepository extends JpaRepository<Tag, Long> {
}
