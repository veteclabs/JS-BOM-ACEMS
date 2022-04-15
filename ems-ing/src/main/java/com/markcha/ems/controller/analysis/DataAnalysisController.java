package com.markcha.ems.controller.analysis;

import com.markcha.ems.controller.GroupController.GroupSearchDto;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping(value="/data")
    public List<HashMap<String, Object>> create(
            GroupSearchDto groupInsertDto,
            HistorySearchDto historySearchDto,
            @RequestParam @Nullable String deviceId
    ) {
        groupInsertDto.setDevoceIdT2(deviceId.replace("\"", ""));
        groupInsertDto.setIsUsage(true);
        groupInsertDto.setEnergyId(null);
        groupInsertDto.setEnergyEqId(null);
        Boolean isDuo = false;
        if (!isNull(groupInsertDto.getTagType())) {
            isDuo = groupInsertDto.getTagType().equals("FLOW") ? true : false;
        }
        if (!isNull(historySearchDto.getUsageType())) {
            if (historySearchDto.getUsageType().equals("PF")) {
                groupInsertDto.setTagType("PF");
                historySearchDto.setUsageType("Usage");
            }
        }
        historySearchDto.setEnergyId(28L);
        groupInsertDto.setEnergyEqId(null);
        historySearchDto.setIsDuo(isDuo);
        historySearchDto.setTagNames(new ArrayList<>());
        historySearchDto.setSecondTagNames(new ArrayList<>());
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds("group");
        List<GroupQueryDto> collect = groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                .map(t->new GroupQueryDto(t, false))
                .peek(t -> historySearchDto.getTagNames().addAll(t.getTagNames()))
                .collect(toList());
//        return collect;
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
    @GetMapping(value="/alarm")
    public Map<String, List<AlarmDto>> show() {
        AlarmMapDto alarmMapDto = new AlarmMapDto();
        alarmMapDto.setTagNames(new ArrayList<>(List.of("5_PWR_KWh","1_PWR_KWh")));
        List<AlarmDto> collect = alarmMapper.getTodayAlarmState(alarmMapDto).stream()
                .map(AlarmDto::new)
                .collect(toList());
        Map<Object, List<HashMap<String, Object>>> tagName = alarmMapper.getTodayAlarmState(alarmMapDto).stream()
                .collect(groupingBy(t -> t.get("tagName")));
        Map<String, List<AlarmDto>> collect1 = collect.stream()
                .collect(groupingBy(t -> t.getTagName()));
        return collect1;
    }
    @Data
    @Getter
    public static class AlarmDto {
        private String tagName;
        private Boolean checkIn;
        private Integer usage;
        private Integer lastUsage;
        private Integer taget;
        private String description;
        public AlarmDto(HashMap<String, Object> object) {
            tagName = (String) object.get("tagName");
            checkIn = (Boolean) object.get("checkIn");
            usage = (Integer) object.get("usage");
            lastUsage = (Integer) object.get("lastUsage");
            taget = (Integer) object.get("taget");
            description = (String) object.get("description");

        }
    }
}
