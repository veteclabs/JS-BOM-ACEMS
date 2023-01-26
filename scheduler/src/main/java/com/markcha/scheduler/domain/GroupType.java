package com.markcha.scheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupType {
    GROUP("그룹",""),
    OBJECT("대상","설비, room, 업체 등"),
    BUILDING("건물", ""),
    LAYER("층",""),
    ROOM("방",""),
    DEPARTMENT("부서",""),
    SUBSTATION_ROOM("변전실",""),
    SWITCHBOARD("배전반",""),
    DISTRIBUTION_BOARD("분전반","");
    private String name;
    private String description;
}
