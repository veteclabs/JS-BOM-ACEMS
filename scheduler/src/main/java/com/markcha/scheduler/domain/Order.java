package com.markcha.scheduler.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name="\"order\"")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;
    @Column(name="\"order\"")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="week_mapper_id")
    private WeekMapper weekMapper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="groups_id")
    private Group group;

}
