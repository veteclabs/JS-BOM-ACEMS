package com.markcha.ems.controller.analysis;

import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.GroupType;
import com.markcha.ems.dto.device.DeviceDto;
import com.markcha.ems.mapper.alarm.AlarmMapDto;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.mapper.analysis.DataMapper;
import com.markcha.ems.mapper.analysis.HistorySearchDto;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.sun.istack.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.markcha.ems.domain.QDevice.device;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class DataAnalysisController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;

    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final DataMapper dataMapper;
    private final AlarmMapper alarmMapper;
    private final DeviceDslRepositoryImpl deviceDslRepository;

    @GetMapping(value="/data/type")
    public List<DeviceDto> create(
            @RequestParam String type
    ) {
        return deviceDslRepository.findAllByType(type).stream()
                .map(DeviceDto::new)
                .collect(toList());
    }
    @GetMapping(value="/data", headers="X-API-VERSION=1")
    public List<HashMap<String, Object>> create(
            GroupSearchDto groupInsertDto,
            HistorySearchDto historySearchDto,
            @RequestParam @Nullable String deviceId
    ) {
        groupInsertDto.setDevoceIdT2(deviceId.replace("\"", ""));
        groupInsertDto.setEnergyId(null);
        groupInsertDto.setEnergyEqId(null);
        Boolean isDuo = false;
        if (!isNull(groupInsertDto.getTagType())) {
            isDuo = groupInsertDto.getTagType().equals("FLOW") ? true : false;
        }
        if (!isNull(historySearchDto.getUsageType())) {
            if (historySearchDto.getUsageType().equals("PF")) {
                groupInsertDto.setTagType("PF");
                historySearchDto.setIsPF(true);
            } else {
                historySearchDto.setIsPF(false);
            }
        }


        groupInsertDto.convertTagTypeName();
        historySearchDto.setEnergyId(28L);
        groupInsertDto.setEnergyEqId(null);
        historySearchDto.setIsDuo(isDuo);

        historySearchDto.setTagNames(new ArrayList<>());
        historySearchDto.setSecondTagNames(new ArrayList<>());
        historySearchDto.setTagNames(deviceDslRepository.findAllByTagTypeAndIdsV2(groupInsertDto.getDeviceEqId(), groupInsertDto.getTagEqType()).stream()
                .map(t->t.getTagName())
                .collect(toList()));
        if(isDuo) {
            groupInsertDto.setTagType("PWR_KWh");
            groupInsertDto.setDeviceEqId(null);
            List<Device> pawerDeviceById = deviceDslRepository.getPawerDeviceById(groupInsertDto.getDeviceIdT());
            List<Long> ids = pawerDeviceById.stream()
                    .map(t->t.getId())
                    .collect(toList());
            historySearchDto.setSecondTagNames(deviceDslRepository.findAllByTagTypeAndIdsV2(!isNull(ids)? device.id.in(ids): device.id.eq(-1L),
                            groupInsertDto.getTagEqType()).stream()
                    .map(t->t.getTagName())
                    .collect(toList()));
        }
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        if(historySearchDto.getTimeType().equals("H")) {
            return dataMapper.getHistoryHour(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("D")) {
            return dataMapper.getHistoryDay(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("15min")) {
            return dataMapper.getHistoryMin(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("M")) {
            return dataMapper.getHistoryMonth(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("Y")) {
            return dataMapper.getHistoryYear(historySearchDto);
        }
        return null;
    }

    @GetMapping(value="/data", headers="X-API-VERSION=2")
    public List<HashMap<String, Object>> show(
            GroupSearchDto groupInsertDto,
            HistorySearchDto historySearchDto,
            @RequestParam @Nullable String deviceId
    ) {
        groupInsertDto.setDevoceIdT2(deviceId.replace("\"", ""));
        groupInsertDto.setEnergyId(null);
        groupInsertDto.setEnergyEqId(null);
        Boolean isDuo = false;
        if (!isNull(groupInsertDto.getTagType())) {
            isDuo = groupInsertDto.getTagType().equals("FLOW") ? true : false;
        }
        if (!isNull(historySearchDto.getUsageType())) {
            if (historySearchDto.getUsageType().equals("PF")) {
                groupInsertDto.setTagType("PF");
            }
        }


        groupInsertDto.convertTagTypeName();
        historySearchDto.setEnergyId(28L);
        groupInsertDto.setEnergyEqId(null);
        historySearchDto.setIsDuo(isDuo);
        historySearchDto.setTagNames(new ArrayList<>());
        historySearchDto.setSecondTagNames(new ArrayList<>());
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds(GroupType.GROUP);
        List<GroupQueryDto> collect = groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                .map(t->new GroupQueryDto(t, false))
                .peek(t -> historySearchDto.getTagNames().addAll(t.getTagNames()))
                .collect(toList());

        if(isDuo) {
            groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                    .map(t->new GroupQueryDto(t, false))
                    .peek(t -> historySearchDto.getSecondTagNames().addAll(t.getTagNames()))
                    .collect(toList());
        }
        if(historySearchDto.getTimeType().equals("H")) {
            return dataMapper.getHistoryHour(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("D")) {
            return dataMapper.getHistoryDay(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("15min")) {
            return dataMapper.getHistoryMin(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("M")) {
            return dataMapper.getHistoryMonth(historySearchDto);
        } else if(historySearchDto.getTimeType().equals("Y")) {
            return dataMapper.getHistoryYear(historySearchDto);
        }
        return null;
    }

    @GetMapping(value="/core/{groupId}")
    public List<GroupQueryDto> create(
            GroupSearchDto groupInsertDto,
            @PathVariable("groupId") Long groupId
    ) {
        return groupDynamicRepository.getAnalysisLocations(new ArrayList<>(List.of(groupId)), groupInsertDto, true).stream()
                .map(t->new GroupQueryDto(t, false))
                .collect(toList());
    }
}


