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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class InsertSampleData {
    private final TagValueDataRepository tagValueDataRepository;
    private final TagListDataRepository tagListDataRepository;
    public List<Tag> createTags(EquipmentType type, Device device) {

        List<Tag> tags = new ArrayList<>();
        String deviceUnit = new String(String.format("U%03d", device.getId().intValue()) + "_");
        List<TagList> allByEquipment_type = tagListDataRepository.findAllByEquipment_Type(type);

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
            tags.add(tag);
        });


        List<TagValue> tagValues = new ArrayList<>();
        List<String> tagNames = tags.stream().map(t -> t.getTagName()).collect(toList());
        Random random = new Random();
        List<TagValue> tagValueStream = tagNames.stream().map(t -> {
            TagValue tagValue = new TagValue();
            tagValue.setName(t);
            tagValue.setQuality(1);
            tagValue.setValue(random.nextDouble() * 100);
            return tagValue;
        }).collect(toList());
        tagValueDataRepository.saveAll(tagValueStream);
        return tags;
    }
}
