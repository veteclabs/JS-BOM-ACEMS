package com.markcha.ems.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name ="device")
public class Device {
    @Id
    @Column(name = "device_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "energy_purpose_id")
    private EnergyPurpose energyPurpose;

    private Byte importance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "energy_id")
    private Energy energy;

    @Column(name = "install_dtm")
    private LocalDateTime installDtm;

    @Column(name = "serial_num", length = 50)
    private String SerialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups_id")
    private Group group;

    @Column(name = "name", length = 50)
    @NotNull
    private String name;

    @BatchSize(size=1000)
    @OneToMany(mappedBy = "device",fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "remark", length = 50)
    private String remark;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "updated")
    private LocalDateTime updated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<WeekMapper> weekMappers = new HashSet<>();

    private Double ct;
    private Double pt;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Voltage voltage;
}
