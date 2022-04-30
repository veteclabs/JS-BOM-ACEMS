package com.markcha.scheduler.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.scheduler.domain.EquipmentType;
import com.markcha.scheduler.domain.QDevice;
import com.markcha.scheduler.domain.QEquipment;
import com.markcha.scheduler.domain.QTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Data;

import java.util.List;

import static com.markcha.scheduler.domain.QEnergy.energy;
import static com.markcha.scheduler.domain.QEquipment.equipment;
import static com.markcha.scheduler.domain.QTag.tag;
import static java.util.Objects.isNull;

@Data
public class GroupSearchDto {
    private Integer level;
    private Long energyId;
    private Boolean detail;
    private EquipmentType equipmentType;
    private List<EquipmentType> equipmentTypes;
    private List<String> tagTypes;
    private String tagType;
    private Boolean isUsage;
    private Long deviceIdT;


    @JsonIgnore
    private BooleanExpression equipmentEqType;
    @JsonIgnore
    private BooleanExpression energyEqId;
    @JsonIgnore
    private BooleanExpression tagEqType;
    @JsonIgnore
    private BooleanExpression tagEqIsUsage;
    @JsonIgnore
    private BooleanExpression deviceEqId;
    @JsonIgnore
    private BooleanExpression tagInTypes;
    public GroupSearchDto() {
        if(!isNull(equipmentType)) this.equipmentEqType = equipment.type.eq(equipmentType);
        if(!isNull(energyId)) this.energyEqId = energy.id.eq(energyId);
        if(!isNull(tagType)) this.tagEqType = tag.type.eq(tagType);
        if(!isNull(tagTypes)) this.tagInTypes = tag.type.in(tagTypes);
    }
    public GroupSearchDto(Integer level, Long energyId, Boolean detail, EquipmentType equipmentType, String tagType, Boolean isUsage, Long deviceId) {
        this.level = level;
        this.energyId = energyId;
        this.detail = detail;
        this.equipmentType = equipmentType;
        this.tagType = tagType;
        if(!isNull(equipmentType)) this.equipmentEqType = equipment.type.eq(equipmentType);
        if(!isNull(energyId)) this.energyEqId = energy.id.eq(energyId);
        if(!isNull(tagType)) this.tagEqType = tag.type.eq(tagType);
        if(!isNull(tagTypes)) this.tagInTypes = tag.type.in(tagTypes);
    }
    public void setIsUsage(Boolean isUsage) {
        this.isUsage = isUsage;
        this.tagEqIsUsage = tag.isUsage.eq(isUsage);
    }
    public void setTagType(String tagType) {
        this.tagType = tagType;
        this.tagEqType = tag.type.eq(tagType);
    }
    public void setTagTypes(List<String> tagTypes) {
        this.tagTypes = tagTypes;
        this.tagInTypes = tag.type.in(tagTypes);
    }
    public void setDevoceIdT2(String deviceIdT) {
        if(!isNull(deviceIdT) && !deviceIdT.equals("AU")) this.deviceIdT = Long.parseLong(deviceIdT);
        if(!deviceIdT.equals("AU")) this.deviceEqId = QDevice.device.id.eq(this.deviceIdT);
    }
    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        this.equipmentEqType = equipment.type.eq(equipmentType);
    }
    public void convertTagTypeName() {
        switch(this.tagType) {
            case "PF":
                setTagType("PWR_PF");
                break;
            case "KWH":
                setTagType("PWR_KWh");
                break;
            case "FLOW":
                setTagType("AIR_Con");
                break;
        }
    }
}