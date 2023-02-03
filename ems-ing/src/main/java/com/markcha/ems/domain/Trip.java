package com.markcha.ems.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="trip_code_id")
    private Long id;
    private Integer code;
    @Column(length = 110)
    private String message;
    private Boolean isTrip;
    private Boolean isWarning;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
    private List<Alarm> alarms = new ArrayList<Alarm>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="equipment_id")
    private Equipment equipment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_list_id")
    private TagList tagList;
}
