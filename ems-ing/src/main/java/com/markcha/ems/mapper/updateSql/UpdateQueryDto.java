package com.markcha.ems.mapper.updateSql;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UpdateQueryDto {

    private List<String> systemTagTypes = new ArrayList<>();
    private List<String> mainTagTypes = new ArrayList<>();
    private List<String> detailTagTypes = new ArrayList<>();
    private List<String> groupTagTypes = new ArrayList<>();
    public UpdateQueryDto() {
        // 시스템 태그 리스트 추가
        systemTagTypes = new ArrayList<>(List.of(
                "COMP_Power",
                "COMP_SystemPre",
                "COMP_AutoStop",
                "COMP_StartPre",
                "COMP_StopPre",
                "COMP_ActTripCode",
                "COMP_ActWarCode",
                "COMP_Trip",
                "COMP_Warning",
                "COMP_LoadFactor"
        ));
        // 주요 태그 리스트 추가
        mainTagTypes = new ArrayList<>(List.of(
                "COMP_SystemPre",
                "COMP_PkgDisTemp",
                "COMP_PkgInTemp",
                "COMP_RunHours",
                "COMP_PackInTemp",
                "COMP_StopPre"
        ));
        // 자세한 태그 리스트 추가
        detailTagTypes = new ArrayList<>(systemTagTypes);
        detailTagTypes.addAll(mainTagTypes);
        groupTagTypes = new ArrayList<>(mainTagTypes);
        groupTagTypes.add("COMP_Power");
    }
}
