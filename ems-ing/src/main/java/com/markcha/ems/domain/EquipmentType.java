package com.markcha.ems.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentType {
    AIR_COMPRESSOR("에어 컴프레셔", "airCompressor"),
    POWER_METTER("전력계", "power"),
    PRESSURE_GAUGE("압력계", "airCompressor"),
    FLOW_METER("유량계", "flow"),
    THERMO_METER("온도계", "temperature"),
    HYGRO_METER("습도계", "humidity"),
    THERMO_HYGROMETER("온습도계", "e");
    private String description;
    private String nickname;
}
