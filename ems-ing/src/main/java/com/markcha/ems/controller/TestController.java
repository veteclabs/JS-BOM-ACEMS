package com.markcha.ems.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.GroupType;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.mapper.alarm.AlarmMapper;
import com.markcha.ems.mapper.analysis.HistorySearchDto;
import com.markcha.ems.mapper.historyHour.HistoryHourMapper;
import com.markcha.ems.mapper.historyHour.HistoryHourSearchDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Stream;

import static com.markcha.ems.domain.QTag.tag;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final HistoryHourMapper historyHourMapper;
    private final WebaccessApiServiceImpl webaccessApiService;

    @GetMapping(value="/totalValue")
    public Map<String, Double> totalValue() {
        ResponseToTalValues result = new ResponseToTalValues();
        Map<String, Double> resultMap = new HashMap<>();
        culcTotalRealTimeValue(result, "PWR_KW");
        culcTotalRealTimeValue(result, "AIR_Flow");
        culcTotalRealTimeValue(result, "AIR_PRE");

        result.getTypes().forEach((key,value) -> {
            resultMap.put(key, value.getValue());
        });

        return resultMap;

    }

    private void culcTotalRealTimeValue(ResponseToTalValues result,String tagType) {

        List<String> tagNames = tagDataRepository.findAllByType(tagType).stream()
                .map(t->t.getTagName())
                .collect(toList());
        result.getTypes().put(tagType, new Type(tagNames));
        result.getTypes().get(tagType).setUnits(webaccessApiService.getTagValuesV2(tagNames));
        Stream<Double> objectStream = result.getTypes().get(tagType).getUnits().values().stream()
                .filter(t -> {
                    Double aDouble = new Double(t.toString());
                    if (!(-113 <= aDouble && aDouble <= -100)) {
                        return true;
                    } else {
                        return false;
                    }
                }).map(t->new Double(t.toString()));
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

    @Autowired
    private TagDataRepository tagDataRepository;
    @GetMapping(value="/specificalPower")
    public double specificalPower() {
        GroupController.GroupSearchDto groupSearchDto = new GroupController.GroupSearchDto();
        List<Tag> powerTags = tagDataRepository.findAllByType("PWR_KWh");
        Double powerValue = powerTags.stream()
                .map(t -> {
                    String tagName = t.getTagName();
                    Double historyHour = historyHourMapper.getHistoryHour(tagName);
                    if (isNull(historyHour)) {
                        return 0.0;
                    } else {
                        return historyHour;
                    }
                }).mapToDouble(t -> t).sum();
        List<Tag> flowTags = tagDataRepository.findAllByType("PWR_KWh");
        Double flowValue = flowTags.stream()
                .map(t -> {
                    String tagName = t.getTagName();
                    Double historyHour = historyHourMapper.getHistoryHour(tagName);
                    if (isNull(historyHour)) {
                        return 0.0;
                    } else {
                        return historyHour;
                    }
                }).mapToDouble(t -> t).sum();


        return Math.round(powerValue/flowValue);

    }
    @GetMapping(value="/totalValue2")
    public List<GroupQueryDto> show2(
            GroupController.GroupSearchDto groupInsertDto
    ) {
        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds(GroupType.GROUP);

        return groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                .map(GroupQueryDto::new).collect(toList());

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
