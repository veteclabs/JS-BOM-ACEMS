package com.markcha.ems.domain;

import com.markcha.ems.dto.week.WeekGroupDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name="schedule")
public class Schedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long id;

    @Column(name="\"interval\"")
    private Integer interval;
    private Boolean isActive;
    private Boolean isGroup;

    @Column(length = 10)
    private String type;

    @Column(name="start_time", columnDefinition = "TIME")
    private LocalTime startTime;
    @Column(name="stop_time", columnDefinition = "TIME")
    private LocalTime stopTime;
    @Column(name="updated", columnDefinition = "DATETIME")
    private LocalDateTime updated;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule",cascade = CascadeType.ALL)
    private Set<WeekMapper> weekMappers = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DayOfWeekMapper> dayOfWeekMappers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    private Set<Group> groups = new HashSet<>();
    private Double min;
    private Double max;

    @Transient
    private List<WeekMapper> weeks = new ArrayList<>();
    @Transient
    private List<Group> standByGroups = new ArrayList<>();
}
