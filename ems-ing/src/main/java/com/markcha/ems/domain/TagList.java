package com.markcha.ems.domain;

import com.markcha.ems.domain.pattern.Pattern;
import com.markcha.ems.domain.pattern.PatternList;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList", cascade = CascadeType.ALL)
    private Set<TagSetMapper> tagSetMappers = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList", cascade = CascadeType.ALL)
    private Set<Tag> tags = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList", cascade = CascadeType.ALL)
    private Set<PatternList> patternList = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagList", cascade = CascadeType.ALL)
    private Set<Trip> trips = new HashSet<>();

    private Double min;
    private Double max;
    private String testType;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        TagList tagList = (TagList) o;
        return Objects.equals(this.isAlarm, tagList.isAlarm) &&
                Objects.equals(this.isTrend, tagList.isTrend) &&
                Objects.equals(this.loggingTime, tagList.loggingTime) &&
                Objects.equals(this.nickname, tagList.nickname) &&
                Objects.equals(this.tagDescription, tagList.tagDescription) &&
                Objects.equals(this.isUsage, tagList.isUsage) &&
                Objects.equals(this.type, tagList.type) &&
                Objects.equals(this.unit, tagList.unit);
    }
}
