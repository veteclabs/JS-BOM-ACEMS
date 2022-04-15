package com.markcha.ems.dto.device;

import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Group;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class AirCompressorDto {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
    private Boolean alarm;
    private String alarmMention;
    private EquipmentDto equipment;
    public AirCompressorDto(Device device) {
        if(!isNull(device)) {
            if (!isNull(device.getGroup())) {
                Group childGroup = device.getGroup();
                id = childGroup.getId();
                name = childGroup.getName();
                if (!isNull(childGroup.getParent())) {
                    groupId = childGroup.getParent().getId();
                    groupName = childGroup.getParent().getName();
                }
            }
            if (!isNull(device.getEquipment())) {
                equipment = new EquipmentDto(device.getEquipment());
            }
            this.alarm = true;
            this.alarmMention = "테스트 메시지";
        }
    }
}