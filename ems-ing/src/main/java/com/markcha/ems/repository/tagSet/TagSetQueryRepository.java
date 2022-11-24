package com.markcha.ems.repository.tagSet;

import com.markcha.ems.domain.TagSet;

import java.util.List;

public interface TagSetQueryRepository {
    List<TagSet> findAllInNickname(List<String> nicknames);
}
