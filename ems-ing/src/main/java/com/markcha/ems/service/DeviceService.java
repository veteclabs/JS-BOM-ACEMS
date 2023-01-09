package com.markcha.ems.service;


import com.markcha.ems.controller.CompressorController.CompressorInsertDto;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.dto.device.DeviceConDto;

import java.util.List;

public interface DeviceService {
    Boolean createDevice(DeviceInsertDto deviceInsert);
    Boolean updateDevice(DeviceInsertDto deviceInsert);
    Boolean createCompressor(CompressorInsertDto compressorInsertDto);
    Boolean updateCompressor(CompressorInsertDto compressorInsertDto);


    List<DeviceConDto> getAllDryers(List<String> tagSetNames);
}
