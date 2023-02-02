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
                "COMP_StopPre",
                "COMP_PackInTemp",
                "PWR_V",
                "PWR_PF",
                "PWR_KWh",
                "PWR_KW",
                "PWR_Kvarh",
                "PWR_A",
                "COMP_SystemPre",
                "COMP_StartPre",
                "COMP_StopPre",
                "COMP_SystemPre",
                "COMP_OilPress",
                "COMP_OilTemp",
                "COMP_SystemPre",
                "COMP_SystemPre",
                "COMP_PercentCap",
                "COMP_RunHours",
                "COMP_2SInletTemp",
                "COMP_DischargPre",
                "COMP_LoadHours",
                "DRY_CoolingTime",
                "DRY_DepressTime",
                "DRY_DesicUseTime",
                "DRY_Dewpoint",
                "DRY_DyingTime",
                "DRY_FiltUseTime",
                "DRY_HeatingTime",
                "DRY_HeatInTemp",
                "DRY_HeatOutTemp",
                "DRY_LTowerTemp",
                "DRY_RepressTime",
                "DRY_RTowerTemp",
                "DRY_RunningTime",
                "DRY_StandbyTime",
                "DRY_ValveUseTime",
                "DRY_AFTDPHI",
                "DRY_Alarm",
                "DRY_BlowerMC",
                "DRY_BlowerMCF",
                "DRY_BlowerMotor",
                "DRY_BlowerOver",
                "DRY_CommonAlarm",
                "DRY_Cooling",
                "DRY_Depress",
                "DRY_DesicTimeOv",
                "DRY_DewPointArm",
                "DRY_DewPointHigh",
                "DRY_DryerStop",
                "DRY_EmerStop",
                "DRY_EmerStopArm",
                "DRY_FastMode",
                "DRY_FilterTimeOv",
                "DRY_Heater1MCF",
                "DRY_Heater2MCF",
                "DRY_HeaterMC1",
                "DRY_HeaterMC1Aux",
                "DRY_HeaterMC2",
                "DRY_HeaterMC2Aux",
                "DRY_HeaterOver",
                "DRY_HeateTrip",
                "DRY_Heating",
                "DRY_LDepress",
                "DRY_LocalFault",
                "DRY_LocalRemoteS",
                "DRY_LocalStatus",
                "DRY_LowTemp",
                "DRY_LPurge",
                "DRY_LRepress",
                "DRY_LTowerCool",
                "DRY_LTowerDepre",
                "DRY_LTowerDepreF",
                "DRY_LTowerDrying",
                "DRY_LTowerHeat",
                "DRY_LTowerHiPre",
                "DRY_LTowerInlet",
                "DRY_LTowerLowPre",
                "DRY_LTowerRepre",
                "DRY_LTowerRepreF",
                "DRY_LTowerStand",
                "DRY_LTowerSwitch",
                "DRY_OverTempIn",
                "DRY_OverTempOut",
                "DRY_PreDPHI",
                "DRY_RDepress",
                "DRY_RemoteStart",
                "DRY_RemoteStop",
                "DRY_Repress",
                "DRY_RPurge",
                "DRY_RRepress",
                "DRY_RTowerCool",
                "DRY_RTowerDepre",
                "DRY_RTowerDepreF",
                "DRY_RTowerDrying",
                "DRY_RTowerHeat",
                "DRY_RTowerHiPre",
                "DRY_RTowerInlet",
                "DRY_RTowerLowPre",
                "DRY_RTowerRepre",
                "DRY_RTowerRepreF",
                "DRY_RTowerStand",
                "DRY_RTowerSwitch",
                "DRY_RunFault",
                "DRY_RunStatus",
                "DRY_Standby",
                "DRY_TC1Inside",
                "DRY_TC2Outlet",
                "DRY_TC3LTower",
                "DRY_TC3RTower",
                "DRY_ValveTimeOv"
        ));
        // 자세한 태그 리스트 추가
        detailTagTypes = new ArrayList<>(systemTagTypes);
        detailTagTypes.addAll(mainTagTypes);
        groupTagTypes = new ArrayList<>(mainTagTypes);
        groupTagTypes.add("COMP_Power");
    }
}
