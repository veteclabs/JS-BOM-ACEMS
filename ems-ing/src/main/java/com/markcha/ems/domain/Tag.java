package com.markcha.ems.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="tag")
public class Tag {
    @Id
    @Column(name = "tag_id", nullable = false)
    private Long id;
    @Column(name="tagname", length = 32)
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "tag_description", length = 255)
    private String tagDescription;
    @Column(name = "unit", length = 10)
    private String unit;
    @Column(name = "unit_conversion")
    private Boolean unitConversion;

    @Column(name = "is_usage")
    private Boolean isUsage;

    @Column(name = "is_trend")
    private Boolean isTrend;

    @Column(name = "logging_time")
    private Integer loggingTime;
    @Column(name = "nickname", length = 50)
    private String nickname;
    @Column(name = "is_alarm")
    private Boolean isAlarm;


}
