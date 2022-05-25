package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.GroupType;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.mapper.analysis.HistorySearchDto;
import com.markcha.ems.repository.*;
import com.markcha.ems.repository.dayofweekmapper.impl.DayOfWeekMapperDslRepositoryImpl;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.weekmapper.impl.WeekMapperDslRepositoryImpl;
import com.markcha.ems.service.InsertSampleData;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import com.sun.istack.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final WebaccessApiServiceImpl webaccessApiService;

    @GetMapping(value="/totalValue")
    public Map<String, Double> show(
            GroupController.GroupSearchDto groupInsertDto
    ) {
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds(GroupType.GROUP);
        ResponseToTalValues result = new ResponseToTalValues();
        Map<String, Double> resultMap = new HashMap<>();
        culcTotalRealTimeValue(groupInsertDto, rootGroupIds, result, "PWR_KW");
        culcTotalRealTimeValue(groupInsertDto, rootGroupIds, result, "AIR_PRE");
        culcTotalRealTimeValue(groupInsertDto, rootGroupIds, result, "AIR_Flow");
        result.getTypes().forEach((key,value) -> {
            resultMap.put(key, value.getValue());
        });
        return resultMap;

    }
    @GetMapping(value="/totalValue2")
    public List<GroupQueryDto> show2(
            GroupController.GroupSearchDto groupInsertDto
    ) {
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds(GroupType.GROUP);

        return groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                .map(GroupQueryDto::new).collect(toList());

    }

    private void culcTotalRealTimeValue(GroupController.GroupSearchDto groupInsertDto, List<Long> rootGroupIds, ResponseToTalValues result,String tagType) {
        groupInsertDto.setTagType(tagType);
        System.out.println(groupInsertDto.getTagType());
        System.out.println(groupInsertDto.getTagEqType());
        List<Group> analysisLocations = groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true);
        System.out.println(analysisLocations.stream()
                .map(GroupQueryDto::new).collect(toList()));
        List<String> tagNames = analysisLocations.stream()
                .map(t -> {
                    GroupQueryDto groupQueryDto = new GroupQueryDto(t);
                    return groupQueryDto.getTagNames();
                }).flatMap(List::stream)
                .collect(toList());
        result.getTypes().put(tagType, new Type(tagNames));
        result.getTypes().get(tagType).setUnits(webaccessApiService.getTagValuesV2(tagNames));
        Stream<Double> objectStream = result.getTypes().get(tagType).getUnits().values().stream()
                .map(t -> {
                    Double aDouble = new Double(t.toString());
                    return aDouble;
                });
        switch(tagType) {
            case "PWR_KW":
                result.getTypes().get(tagType).setValue(objectStream.mapToDouble(a->a).sum());
                break;
            case "AIR_PRE":
                result.getTypes().get(tagType).setValue(objectStream.mapToDouble(a->a).average().getAsDouble());
                break;
            case "AIR_Flow":
                result.getTypes().get(tagType).setValue(objectStream.mapToDouble(a->a).average().getAsDouble());
                break;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ResponseToTalValues {
        private Map<String, Type> types = new HashMap<>();


    }
    @Data
    @NoArgsConstructor
    public static class Type {
        private List<String> tagNames;
        private Double value;
        @JsonIgnore
        private Map<String, Object> units = new HashMap<>();
        public Type(List<String> tagNames) {
            this.tagNames = tagNames;
        }
    }
}
