package com.markcha.ems.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupType {
    GROUP("그룹"),
    AIR_COMPRESSOR("에어 컴프레셔");
    private String description;
}
