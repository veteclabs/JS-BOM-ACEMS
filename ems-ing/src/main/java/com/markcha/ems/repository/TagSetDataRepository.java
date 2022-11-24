package com.markcha.ems.repository;

import com.markcha.ems.domain.TagSet;
import com.markcha.ems.repository.tagSet.TagSetQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagSetDataRepository extends JpaRepository<TagSet, Long>, TagSetQueryRepository {
}
