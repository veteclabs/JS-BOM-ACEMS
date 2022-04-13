package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.EquipmentDataRepository;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.DeviceService;
import com.markcha.ems.service.InsertSampleData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final EquipmentDslRepositoryImpl equipmentDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final InsertSampleData insertSampleData;


    @Override
    public Boolean createDevice(DeviceInsertDto deviceInsert) {
        Device newDevice = new Device();

        Equipment selectedEquipoment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                deviceInsert.getModel());
        if(!isNull(deviceInsert.getGroupId())) {
            Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());
            newDevice.setGroup(seletedGroup);
        }
        newDevice.setName(deviceInsert.getName());
        newDevice.setEquipment(selectedEquipoment);
        newDevice.setCt(deviceInsert.getCt());
        newDevice.setPt(deviceInsert.getPt());
        newDevice.setVoltage(deviceInsert.getVoltage());
        Device save = deviceDataRepository.save(newDevice);
        List<Tag> tags = insertSampleData.createTags(selectedEquipoment.getType(), save);
        newDevice.setTags(new HashSet<>(tags));
        deviceDataRepository.save(save);
        return true;
    }
    @Override
    public Boolean updateDevice(DeviceInsertDto deviceInsert) {
        Device seletedDevice = deviceDslRepository.getOneById(deviceInsert.getId());

        Equipment selectedEquipoment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                deviceInsert.getModel());
        if(!isNull(deviceInsert.getGroupId())) {
            Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());
            seletedDevice.setGroup(seletedGroup);
        }

        seletedDevice.setName(deviceInsert.getName());
        seletedDevice.setEquipment(selectedEquipoment);
        seletedDevice.setCt(deviceInsert.getCt());
        seletedDevice.setPt(deviceInsert.getPt());
        seletedDevice.setVoltage(deviceInsert.getVoltage());
        deviceDataRepository.save(seletedDevice);
        return true;
    }

    @Override
    public Boolean createCompressor(CompressorController.CompressorInsertDto compressorInsertDto) {
        return null;
    }

    @Override
    public Boolean updateCompressor(CompressorController.CompressorInsertDto compressorInsertDto) {
        return null;
    }
}
