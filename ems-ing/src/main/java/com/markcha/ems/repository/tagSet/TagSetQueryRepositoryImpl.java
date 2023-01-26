package com.markcha.ems.repository.tagSet;

import com.markcha.ems.domain.TagSet;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.markcha.ems.domain.QTagSet.tagSet;

@Repository
public class TagSetQueryRepositoryImpl implements TagSetQueryRepository {
    @Autowired
    private JPAQueryFactory query;
    @Override
    public List<TagSet> findAllInNickname(List<String> nicknames) {
        return query.selectFrom(tagSet)
                .where(tagSet.nickname.in(nicknames))
                .fetch();
    }
}
