package com.markcha.ems.controller;

import com.markcha.ems.domain.Equipment;
import com.markcha.ems.dto.device.TemplcateDto;
import com.markcha.ems.repository.DeviceDataRepository;
import com.markcha.ems.repository.EquipmentDataRepository;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.DeviceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EquipmentController {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    private final GroupDslRepositoryImpl groupDslRepository;
    private final DeviceServiceImpl deviceService;
    private final DeviceDataRepository deviceDataRepository;
    private final EquipmentDataRepository equipmentDataRepository;
    private final EquipmentDslRepositoryImpl equipmentDslRepository;
    @GetMapping(value="/etcss")
    public void etc(
    ) {
        Equipment a = new Equipment();
        a.setId(100L);
        a.setName("few");
        a.setType(AIR_COMPRESSOR);
        equipmentDataRepository.save(a);
    }
}
