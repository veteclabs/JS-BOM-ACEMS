package com.markcha.ems.domain.pattern;


import com.markcha.ems.domain.TagList;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PatternList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pattern_list_id", nullable = false)
    private Long id;
    @Column(length = 50)
    private String name;
    @Column(length = 100)
    private String description;
    private Double remoteMinValue;
    private Double remoteMaxValue;
    @Column(length = 8)
    @Enumerated(EnumType.STRING)
    private IOType IOType;
    @Column(length = 200)
    private String formula;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patternList", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pattern> patterns = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_list_id")
    private TagList tagList;
    @Override
    public int hashCode() {
        if(!isNull(this.id)) {
            return this.id.hashCode();
        }
        return 0;
    }
}
