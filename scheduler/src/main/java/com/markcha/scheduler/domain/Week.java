package com.markcha.scheduler.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
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
