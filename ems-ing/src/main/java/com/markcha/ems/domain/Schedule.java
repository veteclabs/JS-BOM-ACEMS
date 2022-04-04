package com.markcha.ems.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private LocalTime stotTime;
    @Column(name="updated", columnDefinition = "DATETIME")
    private LocalDateTime updated;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    private Set<WeekMapper> weekMappers = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    private Set<DayOfWeekMapper> dayOfWeekMappers = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    private Set<Group> groups = new HashSet<>();
}
