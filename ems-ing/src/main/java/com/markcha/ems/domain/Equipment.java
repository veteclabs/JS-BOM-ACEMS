package com.markcha.ems.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Equipment")
public class Equipment {
    @Id
    @Column(name = "equipment_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "updated")
    private LocalDateTime updated;

    @OneToMany(mappedBy="equipment",fetch = FetchType.LAZY)
    private Set<Device> equipmentMgmt = new HashSet<>();


}
