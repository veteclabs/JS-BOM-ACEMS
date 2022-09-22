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
    @Column(name = "name", length = 150)
    private String name;
    @Column(name = "type", length = 70)
    private EquipmentType type;
    @Column(name = "maker", length = 70)
    private String maker;
    @Column(name = "model", length = 70)
    private String model;
    @Column(name = "description", length = 70)
    private String description;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "updated")
    private LocalDateTime updated;

    @OneToMany(mappedBy="equipment",fetch = FetchType.LAZY)
    private Set<Device> devices = new HashSet<>();

    @OneToMany(mappedBy = "equipment", fetch = FetchType.LAZY)
    private Set<TagList> tagLists = new HashSet<>();
}
