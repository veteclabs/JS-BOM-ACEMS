package com.markcha.ems.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
public class Alarm {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="alarm_id")
    private Long id;
    @Column(length = 110)
    private String message;
    @Column(columnDefinition = "DATE")
    private LocalDate eventDate;
    @Column(columnDefinition = "TIME")
    private LocalTime occurrenceTime;
    private Boolean checkIn;
    @Column(columnDefinition = "TIME")
    private LocalTime recoverTime;
    @Column(columnDefinition = "DATE")
    private LocalDate recoverDate;
    @Column(length = 10)
    private String type;
    private Double tempValue;
    private Double prssValue;
    private Double kwhValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_id")
    private Tag tag;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_id")
    private Trip trip;
}
