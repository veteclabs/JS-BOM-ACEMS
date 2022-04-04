package com.markcha.ems.controller;

import com.markcha.ems.dto.device.CompressorDto;
import com.markcha.ems.dto.device.TemplcateDto;
import com.markcha.ems.repository.device.DeviceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DeviceController {
    private final DeviceRepository deviceDslRepository;

    public DeviceController(@Qualifier("deviceDslRepositoryImpl") DeviceRepository deviceDslRepository) {
        this.deviceDslRepository = deviceDslRepository;
    }

    @GetMapping(value="/templcates", headers="X-API-VERSION=1")
    public List<TemplcateDto> templcate(
    ) {
        return deviceDslRepository.findAllTemplcates("templcate").stream()
                .map(TemplcateDto::new)
                .collect(Collectors.toList());
    }
    @GetMapping(value="/compressors", headers="X-API-VERSION=1")
    public List<CompressorDto> compressors(
    ) {
        return deviceDslRepository.findAllCompressors("compressor").stream()
                .map(CompressorDto::new)
                .collect(Collectors.toList());
    }
}
