package com.markcha.ems.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name ="week")
public class Week {
    @Id
    @Column(name = "week_id")
    private Long id;
    private Integer idx;

    @Column(length = 10)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "week")
    private Set<WeekMapper> weekMappers = new HashSet<>();

}
