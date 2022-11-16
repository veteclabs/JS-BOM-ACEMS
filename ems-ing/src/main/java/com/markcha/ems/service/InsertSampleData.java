package com.markcha.ems.service;

import com.markcha.ems.domain.*;
import com.markcha.ems.repository.TagListDataRepository;
import com.markcha.ems.repository.TagValueDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class InsertSampleData {
    private final TagValueDataRepository tagValueDataRepository;
    private final TagListDataRepository tagListDataRepository;
    public List<Tag> createTags(Long equipmentId, Device device, Long unitId) {

        List<Tag> tags = new ArrayList<>();
        String deviceUnit = new String(String.format("U%03d", isNull(unitId) ?new Integer(device.getRemark()): unitId) + "_");
        List<TagList> allByEquipment_type = tagListDataRepository.findAllByEquipmentId(equipmentId);


        List<TagValue> tagValues = new ArrayList<>();
        allByEquipment_type.forEach(t->{
            Tag tag = new Tag();
            tag.setType(t.getType());
            tag.setTagName(deviceUnit + t.getType());
            tag.setIsAlarm(t.getIsAlarm());
            tag.setIsTrend(t.getIsTrend());
            tag.setIsUsage(t.getIsUsage());
            tag.setLoggingTime(t.getLoggingTime());
            tag.setNickname(t.getNickname());
            tag.setShowAble(t.getShowAble());
            tag.setTagDescription(t.getTagDescription());
            tag.setUnit(t.getUnit());
            tag.setUnitConversion(t.getUnitConversion());
            tag.setDevice(device);
            tag.setTagList(t);
            tags.add(tag);
            TagValue tagValue = new TagValue();
            tagValue.setName(deviceUnit + t.getType());
            tagValue.setQuality(1);
            tagValue.setValue(new Double(createRandomValue(t.getTestType(), t.getMin(), t.getMax()).toString()));
            tagValues.add(tagValue);
        });
        tagValueDataRepository.saveAll(tagValues);

        return tags;
    }
    private Object createRandomValue(String type, Double min, Double max) {
        Random random = new Random();
        Object value = null;

        switch(type) {
            case "double":
                value = random.doubles(min,max).findFirst().getAsDouble();
                break;
            case "int":
                max = max+1;
                value = random.ints(new Double(min.toString()).intValue(),new Double(max.toString()).intValue())
                        .findFirst().getAsInt();
                break;

        }
        return value;
    }
}


