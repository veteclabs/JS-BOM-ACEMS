package com.markcha.ems.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@Entity
@Table(name="energy")
public class Energy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "energy_id")
    private Long id;

    private String name;
    @Column(name="MJ")
    private Double MJ;

    @Column(name="kcal")
    private Double kcal;

    @Column(name="toe")
    private Double toe;
    @Column(name="tCO2")
    private Double tCO2;

    @Column(name="usage")
    private Integer usage;


    @BatchSize(size = 300)
    @OneToMany(mappedBy = "energy", fetch = FetchType.LAZY)
    private Set<Device> equipmentMgmts = new HashSet<>();


}
