package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.TagValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagValueDataRepository extends JpaRepository<TagValue, String> {
    @Query(value="select t from TagValue t where t.Name in :names")
    List<TagValue> findAllByNameIn(@Param("names") List<String> names);
}
