package com.markcha.ems.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="groups")
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gruops_id")
    private Long id;

    private Integer level;
    private String name;
    private String purpose;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "gruops_parent_id")
    private Group parent;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    private String type;
    // 자식 정의
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private Set<Group> children = new HashSet<>();

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<Device> deviceSet = new HashSet<>();

    @Transient
    private List<Group> childs = new ArrayList<>();
    @Transient
    private Set<Device> devices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parents")
    private Set<Link> parentLocations = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "childs")
    private Set<Link> childLocations = new HashSet<>();
}
