package com.markcha.ems.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name ="device")
public class Device {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Double ct;
    private Double pt;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Voltage voltage;

    @Transient
    private List<Tag> tagList = new ArrayList<>();
}
