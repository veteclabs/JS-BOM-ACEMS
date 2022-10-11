package com.markcha.ems.repository;

import com.markcha.ems.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TagDataRepository extends JpaRepository<Tag, Long> {
    @Query(value = "select t from Tag t left join t.tagList tl where tl.type = :type")
    List<Tag> findAllByType(@Param("type") String type);
}
