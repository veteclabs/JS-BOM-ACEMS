package com.markcha.scheduler.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="energy_purpose")
public class EnergyPurpose {

    @Id
    @Column(name = "energy_purpose_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "energyPurpose", fetch = FetchType.LAZY)
    private Set<Device> equipmentMgmts = new HashSet<>();
}
