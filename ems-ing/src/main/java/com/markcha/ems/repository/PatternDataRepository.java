package com.markcha.ems.repository;

import com.markcha.ems.domain.pattern.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternDataRepository extends JpaRepository<Pattern, Long> {

}
