package com.markcha.scheduler.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

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
    private GroupType type;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<Order> orders = new HashSet<>();

    @Override
    public int hashCode() {
        if(!isNull(this.id)) {
            return this.id.hashCode();
        }
        return 0;
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
//            return false;
//        Group group = (Group) o;
//        return Objects.equals(id, group.id);
//    }
    @Transient
    private Device tagetDevice;
}
