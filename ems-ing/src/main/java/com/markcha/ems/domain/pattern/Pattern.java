package com.markcha.ems.domain.pattern;

import com.markcha.ems.domain.TagList;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Pattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pettern_id", nullable = false)
    private Long id;
    private Integer beforeSleep;
    private Integer afterSleep;
    private char variableName;
    private Double requireValue;
    @Column(name="\"order\"")
    private Integer order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="pattern_list_id")
    private PatternList patternList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_list_id")
    private TagList tagList;
}
