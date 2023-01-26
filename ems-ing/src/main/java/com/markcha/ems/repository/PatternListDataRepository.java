package com.markcha.ems.repository;

import com.markcha.ems.domain.Week;
import com.markcha.ems.domain.pattern.PatternList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatternListDataRepository extends JpaRepository<PatternList, Long> {

}
