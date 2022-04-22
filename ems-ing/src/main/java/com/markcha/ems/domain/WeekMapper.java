package com.markcha.ems.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name ="week_mapper")
public class WeekMapper {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_mapper_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="schedule_id")
    private Schedule schedule;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="week_id")
    private Week week;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "weekMapper",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();
    @Transient
    private List<Group> standByGroups = new ArrayList<>();

}
