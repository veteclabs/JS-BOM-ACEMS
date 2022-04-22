package com.markcha.ems.repository;

import com.markcha.ems.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDataRepository extends JpaRepository<Tag, Long> {
}
