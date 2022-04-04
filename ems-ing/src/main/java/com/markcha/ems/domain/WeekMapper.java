package com.markcha.ems.domain;

import lombok.Data;

import javax.persistence.*;

@Data
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="groups_id")
    private Group group;
    @Column(name="\"order\"")
    private Integer order;
}
