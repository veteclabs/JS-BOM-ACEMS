package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Equipment;
import com.markcha.ems.domain.Group;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.EquipmentDataRepository;
import com.markcha.ems.repository.device.DeviceRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceDslRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final EquipmentDslRepositoryImpl equipmentDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    public DeviceServiceImpl(DeviceDslRepositoryImpl deviceDslRepository, DeviceDataRepository deviceDataRepository, EquipmentDataRepository equipmentDataRepository, EquipmentDslRepositoryImpl equipmentDslRepository, GroupDslRepositoryImpl groupDslRepository) {
        this.deviceDslRepository = deviceDslRepository;
        this.deviceDataRepository = deviceDataRepository;
        this.equipmentDataRepository = equipmentDataRepository;
        this.equipmentDslRepository = equipmentDslRepository;
        this.groupDslRepository = groupDslRepository;
    }

    @Override
    public Boolean createDevice(DeviceInsertDto deviceInsert) {
        Device newDevice = new Device();

        Equipment selectedEquipoment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                deviceInsert.getModel());

        Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());

        newDevice.setName(deviceInsert.getName());
        newDevice.setEquipment(selectedEquipoment);
        newDevice.setGroup(seletedGroup);
        newDevice.setCt(deviceInsert.getCt());
        newDevice.setPt(deviceInsert.getPt());
        newDevice.setVoltage(deviceInsert.getVoltage());
        deviceDataRepository.save(newDevice);
        return true;
    }
    @Override
    public Boolean updateDevice(DeviceInsertDto deviceInsert) {
        Device seletedDevice = deviceDslRepository.getOneById(deviceInsert.getId());

        Equipment selectedEquipoment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                deviceInsert.getModel());

        Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());

        seletedDevice.setName(deviceInsert.getName());
        seletedDevice.setEquipment(selectedEquipoment);
        seletedDevice.setGroup(seletedGroup);
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
