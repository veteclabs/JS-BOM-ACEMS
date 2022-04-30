package com.markcha.scheduler.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups_parent_id")
    private Group parents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups_child_id")
    private Group childs;


}
