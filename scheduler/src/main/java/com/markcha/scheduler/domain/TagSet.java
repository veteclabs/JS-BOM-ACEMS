package com.markcha.scheduler.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
public class TagSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_set_id", nullable = false)
    private Long id;
    @Column(length = 32)
    private String nickname;
    @Column(length = 32)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagSet")
    private Set<TagSetMapper> tagSetMappers = new HashSet<>();

}
