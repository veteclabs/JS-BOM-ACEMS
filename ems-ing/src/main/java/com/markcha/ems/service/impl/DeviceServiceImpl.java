package com.markcha.ems.service.impl;

import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.domain.*;
import com.markcha.ems.dto.device.DeviceConDto;
import com.markcha.ems.exception.custom.MethodNotAllowedException;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.EquipmentDataRepository;
import com.markcha.ems.repository.TagDataRepository;
import com.markcha.ems.repository.TagListDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.DeviceService;
import com.markcha.ems.service.InsertSampleData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TagListDataRepository tagListDataRepository;


    @Override
    public Boolean createDevice(DeviceInsertDto deviceInsert) {
        Device newDevice = new Device();

        String model = null;
        if (!isNull(deviceInsert.getModel())) {
            model = deviceInsert.getModel().equals("") ? null : deviceInsert.getModel();
        }
        Equipment selectedEquipment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                model);
        if(!isNull(deviceInsert.getGroupId())) {
            Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());
            newDevice.setGroup(seletedGroup);
            if(deviceDslRepository.countingGroupHavePressureDevice(deviceInsert.getGroupId()) >= 1 && deviceInsert.getType().equals("압력계")) {
                throw new MethodNotAllowedException("선택된 그룹은 이미 압력계가 존재합니다.\n(해당 그룹에 2개 이상의 압력계가 존재할 경우 그룹 스케줄 제어가 불가능합니다.)");
            }
        }

        newDevice.setName(deviceInsert.getName());
        newDevice.setEquipment(selectedEquipment);
        newDevice.setCt(deviceInsert.getCt());
        newDevice.setPt(deviceInsert.getPt());
        newDevice.setVoltage(deviceInsert.getVoltage());
        Device save = deviceDataRepository.save(newDevice);
        Long unitId = deviceInsert.getUnitId();
        List<Tag> tags = insertSampleData.createTags(selectedEquipment.getId(), save, unitId);
        newDevice.setTags(new HashSet<>(tags));
        deviceDataRepository.save(save);
        return true;
    }



    @Autowired
    private TagDataRepository tagDataRepository;
    @Override
    public Boolean updateDevice(DeviceInsertDto deviceInsert) {
        Device seletedDevice = deviceDslRepository.getOneById(deviceInsert.getId());

        long selectGroupPressureCount = deviceDslRepository.countingGroupHavePressureDevice(deviceInsert.getGroupId());
        if (selectGroupPressureCount > 0 && isNull(seletedDevice.getGroup()) && deviceInsert.getType().equals("압력계")) {
            throw new MethodNotAllowedException("선택된 그룹은 이미 압력계가 존재합니다.\n(해당 그룹에 2개 이상의 압력계가 존재할 경우 그룹 스케줄 제어가 불가능합니다.)");
        }



        Equipment selectedEquipment = equipmentDslRepository.getOneByTypeAndModel(
                deviceInsert.getType(),
                deviceInsert.getModel());
        Group seletedGroup = groupDslRepository.getOneById(deviceInsert.getGroupId());
        seletedDevice.setGroup(seletedGroup);

        seletedDevice.setName(deviceInsert.getName());
        seletedDevice.setEquipment(selectedEquipment);
        seletedDevice.setCt(deviceInsert.getCt());
        seletedDevice.setPt(deviceInsert.getPt());

        seletedDevice.setVoltage(deviceInsert.getVoltage());
        deviceDataRepository.save(seletedDevice);
        tagDataRepository.deleteAllInBatch(seletedDevice.getTags());
        List<Tag> tags = insertSampleData.createTags(selectedEquipment.getId(), seletedDevice, null);
        tags.stream()
                .forEach(t->{
                    t.setDevice(seletedDevice);
                });
        tagDataRepository.saveAll(tags);
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

    @Override
    public List<DeviceConDto> getAllDryers(List<String> tagSetNames) {
        List<Device> dryers = deviceDslRepository.findAllDeviceGroupByTagSet(tagSetNames);
        return dryers.stream()
                .map(DeviceConDto::of)
                .collect(Collectors.toList());
    }
}
