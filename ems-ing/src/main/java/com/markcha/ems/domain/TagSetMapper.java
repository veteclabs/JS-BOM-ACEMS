package com.markcha.ems.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagSetMapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_set_mapper_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_list_id")
    private TagList tagList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_set_id")
    private TagSet tagSet;
}
