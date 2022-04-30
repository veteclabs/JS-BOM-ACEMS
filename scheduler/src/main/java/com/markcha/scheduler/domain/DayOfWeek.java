package com.markcha.scheduler.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name ="day_of_week")
public class DayOfWeek {
    @Id
    @Column(name = "day_of_week_id")
    private Long id;
    private Integer idx;

    @Column(length = 10)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dayOfWeek")
    private Set<DayOfWeekMapper> dayOfWeekMappers = new HashSet<>();
}
