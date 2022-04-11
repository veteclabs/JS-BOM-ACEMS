package com.markcha.ems.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentType {
    AIR_COMPRESSOR("에어 컴프레셔"),
    WATTMETER("전력계"),
    FLOWMETER("유량계"),
    THERMOMETER("온도계"),
    HYGROMETER("습도계"),
    THERMO_HYGROMETER("옵습도계");
    private String description;
}
