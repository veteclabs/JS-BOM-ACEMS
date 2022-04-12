package com.markcha.ems.service;

import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.domain.TagValue;
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
    public List<Tag> createTags(String type, Device device) {

        List<Tag> tags = new ArrayList<>();
        String deviceUnit = new String(String.format("%03d", device.getId().intValue()) + "_");

        switch(type) {
            case "compressor":
                Tag rpm = new Tag();
                rpm.setIsAlarm(false);
                rpm.setIsTrend(true);
                rpm.setIsUsage(false);
                rpm.setLoggingTime(300);
                rpm.setNickname("rpm");
                rpm.setShowAble(true);
                rpm.setTagDescription("가동률");
                rpm.setUnit("%");
                rpm.setTagName(deviceUnit + "RPM");
                rpm.setUnitConversion(null);
                rpm.setType("RPM");
                rpm.setDevice(device);
                tags.add(rpm);

                Tag power = new Tag();
                power.setIsAlarm(false);
                power.setIsTrend(true);
                power.setIsUsage(false);
                power.setLoggingTime(300);
                power.setNickname("power");
                power.setShowAble(true);
                power.setTagDescription("파워");
                power.setUnit("");
                power.setTagName(deviceUnit + "POWER");
                power.setUnitConversion(null);
                power.setDevice(device);
                power.setType("POWER");
                tags.add(power);

                Tag state = new Tag();
                state.setIsAlarm(false);
                state.setIsTrend(true);
                state.setIsUsage(false);
                state.setLoggingTime(300);
                state.setNickname("state");
                state.setShowAble(true);
                state.setTagDescription("상태");
                state.setUnit(null);
                state.setTagName(deviceUnit + "STATE");
                state.setUnitConversion(null);
                state.setDevice(device);
                state.setType("STATE");
                tags.add(state);

                Tag bar = new Tag();
                bar.setIsAlarm(false);
                bar.setIsTrend(true);
                bar.setIsUsage(false);
                bar.setLoggingTime(300);
                bar.setNickname("pressour");
                bar.setShowAble(true);
                bar.setTagDescription("압력");
                bar.setUnit(null);
                bar.setTagName(deviceUnit + "BAR");
                bar.setUnitConversion(null);
                bar.setDevice(device);
                bar.setType("BAR");
                tags.add(bar);

                Tag airTemp = new Tag();
                airTemp.setIsAlarm(false);
                airTemp.setIsTrend(true);
                airTemp.setIsUsage(false);
                airTemp.setLoggingTime(300);
                airTemp.setNickname("air-temp");
                airTemp.setShowAble(true);
                airTemp.setTagDescription("에어 온도");
                airTemp.setUnit(null);
                airTemp.setTagName(deviceUnit + "AIR_TEMP");
                airTemp.setUnitConversion(null);
                airTemp.setDevice(device);
                airTemp.setType("AIR_TEMP");
                tags.add(airTemp);
                break;

            case "전력":
                Tag elect = new Tag();
                elect.setIsAlarm(false);
                elect.setIsTrend(false);
                elect.setIsUsage(true);
                elect.setLoggingTime(300);
                elect.setNickname("ACTIVE_POWER");
                elect.setShowAble(true);
                elect.setTagDescription("유효 전력");
                elect.setUnit("kWh");
                elect.setTagName(deviceUnit + "KWH");
                elect.setUnitConversion(null);
                elect.setDevice(device);
                elect.setType("KWH");
                tags.add(elect);
                break;

            case "유량계":
                Tag bar2 = new Tag();
                bar2.setIsAlarm(false);
                bar2.setIsTrend(false);
                bar2.setIsUsage(true);
                bar2.setLoggingTime(300);
                bar2.setNickname("flow");
                bar2.setShowAble(true);
                bar2.setTagDescription("유량");
                bar2.setUnit("m3");
                bar2.setTagName(deviceUnit + "FLOW");
                bar2.setUnitConversion(null);
                bar2.setDevice(device);
                bar2.setType("FLOW");
                tags.add(bar2);
                break;

            case "온도계":
                Tag temp = new Tag();
                temp.setIsAlarm(false);
                temp.setIsTrend(true);
                temp.setIsUsage(false);
                temp.setLoggingTime(300);
                temp.setNickname("temperature");
                temp.setShowAble(true);
                temp.setTagDescription("온도");
                temp.setUnit("C");
                temp.setTagName(deviceUnit + "temp");
                temp.setUnitConversion(null);
                temp.setDevice(device);
                temp.setType("TEMP");
                tags.add(temp);
                break;

            case "습도계":
                Tag hum = new Tag();
                hum.setIsAlarm(false);
                hum.setIsTrend(true);
                hum.setIsUsage(false);
                hum.setLoggingTime(300);
                hum.setNickname("humidity");
                hum.setShowAble(true);
                hum.setTagDescription("습도");
                hum.setUnit("m3");
                hum.setTagName(deviceUnit + "hum");
                hum.setUnitConversion(null);
                hum.setDevice(device);
                hum.setType("HUM");
                tags.add(hum);
                break;
        }
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
