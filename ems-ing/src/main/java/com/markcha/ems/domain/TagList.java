package com.markcha.ems.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="tag_list")
public class TagList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_list_id", nullable = false)
    private Long id;
    @Column(name="tagname", length = 32)
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

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
    @Column(name="value")
    private Double value;
    @Column(name = "logging_time")
    private Integer loggingTime;
    @Column(name = "nickname", length = 50)
    private String nickname;
    @Column(name = "is_alarm")
    private Boolean isAlarm;
    @Column(name = "show_able")
    private Boolean showAble;
    @Column(length = 20)
    private String type;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList")
    private Set<TagSetMapper> tagSetMappers = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList")
    private Set<Tag> tags = new HashSet<>();
    private Double min;
    private Double max;
    private String testType;
}
