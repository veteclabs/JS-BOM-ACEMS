package com.markcha.ems.config;

import com.markcha.ems.domain.TagList;
import com.markcha.ems.domain.pattern.IOType;
import com.markcha.ems.domain.pattern.PatternList;
import com.markcha.ems.mapper.updateSql.UpdateMapper;
import com.markcha.ems.mapper.updateSql.UpdateQueryDto;
import com.markcha.ems.repository.PatternDataRepository;
import com.markcha.ems.repository.PatternListDataRepository;
import com.markcha.ems.repository.TagListDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Timer;


@Component
public class DataUpdateRunner implements CommandLineRunner {
    @Autowired
    private UpdateMapper updateMapper;
    @Autowired
    private TagListDataRepository tagListDataRepository;
    @Autowired
    private PatternListDataRepository patternListDataRepository;
    @Autowired
    private PatternDataRepository patternDataRepository;
    @Value("${profile}")
    private String profile;
    @Override
    public void run(String... args) throws Exception {
        if (profile.equals("prod")) {
            UpdateQueryDto updateQueryDto = new UpdateQueryDto();
            updateMapper.updateStateComponent(updateQueryDto);
            updateMapper.updateMainPageComponent(updateQueryDto);
            updateMapper.updateDetailPageComponent(updateQueryDto);
            updateMapper.updateImportantComponent(updateQueryDto);
    //        updateMapper.updateGroupPageComponent();
            updateMapper.deleteTagSetMapperDuplicate(updateQueryDto);
            updateMapper.deleteSroucesHourWithDay(updateQueryDto);
            updateMapper.insertSroucesHourWithDay(updateQueryDto);
            updateMapper.deleteSourcesFiveMinute(updateQueryDto);
            updateMapper.insertSourcesFiveMinute(updateQueryDto);
        }
//        insertActionVariable();
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertActionVariable() {
        List<TagList> allDontHavePatternList = tagListDataRepository.findAllDontHavePatternList("COMP_Power");
        for (TagList tagList : allDontHavePatternList) {
            if(tagList.getPatternList().isEmpty()) {
                String modelName = tagList.getEquipment().getMaker() + " " + tagList.getEquipment().getModel();
                PatternList onPower = PatternList.builder()
                        .description(modelName + " Power ON")
                        .IOType(IOType.INPUT)
                        .name("ON")
                        .remoteMaxValue(1.0)
                        .remoteMaxValue(1.0)
                        .tagList(tagList)
                        .build();
                PatternList offPower = PatternList.builder()
                        .description(modelName + " Power OFF")
                        .IOType(IOType.INPUT)
                        .name("OFF")
                        .remoteMaxValue(0.0)
                        .remoteMaxValue(0.0)
                        .tagList(tagList)
                        .build();
            }
        }
        System.out.println(allDontHavePatternList);



    }
}



