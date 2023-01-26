package com.markcha.ems.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="history")
@IdClass(TagKey.class)
public class History {
    @Id
    @Column(name="timestamp", columnDefinition = "DATETIME")
    private LocalDateTime timestamp;
    @Id
    @Column(name="tagname")
    private String tagname;
    @Column(name="value")
    private Double value;
}
