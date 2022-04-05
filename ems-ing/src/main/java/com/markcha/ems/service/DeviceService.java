package com.markcha.ems.service;


import com.markcha.ems.controller.CompressorController;
import com.markcha.ems.controller.CompressorController.CompressorInsertDto;
import com.markcha.ems.controller.DeviceController;
import com.markcha.ems.controller.DeviceController.DeviceInsertDto;
import com.markcha.ems.domain.Device;

public interface DeviceService {
    public Boolean createDevice(DeviceInsertDto deviceInsert);
    public Boolean updateDevice(DeviceInsertDto deviceInsert);
    public Boolean createCompressor(CompressorInsertDto compressorInsertDto);
    public Boolean updateCompressor(CompressorInsertDto compressorInsertDto);
}
