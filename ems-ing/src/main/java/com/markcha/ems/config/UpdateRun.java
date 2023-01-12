package com.markcha.ems.config;

import com.markcha.ems.mapper.updateSql.UpdateMapper;
import com.markcha.ems.repository.PatternDataRepository;
import com.markcha.ems.repository.PatternListDataRepository;
import com.markcha.ems.repository.TagListDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateRun  extends TimerTask {


    public UpdateRun(UpdateMapper updateMapper, TagListDataRepository tagListDataRepository, PatternListDataRepository patternListDataRepository, PatternDataRepository patternDataRepository) {

        this.updateMapper = updateMapper;
        this.tagListDataRepository = tagListDataRepository;
        this.patternListDataRepository = patternListDataRepository;
        this.patternDataRepository = patternDataRepository;
    }

    private UpdateMapper updateMapper;
    private TagListDataRepository tagListDataRepository;
    private PatternListDataRepository patternListDataRepository;
    private PatternDataRepository patternDataRepository;
    @Override
    public void run() {
        updateMapper.updateStateComponent();
//        updateMapper.updateMainPageComponent();
//        updateMapper.updateDetailPageComponent();
//        updateMapper.updateImportantComponent();
////        updateMapper.updateGroupPageComponent();
//        updateMapper.deleteTagSetMapperDuplicate();
//        updateMapper.deleteSroucesHourWithDay();
//        updateMapper.insertSroucesHourWithDay();
//        updateMapper.deleteSourcesFiveMinute();
//        updateMapper.insertSourcesFiveMinute();
//        insertActionVariable();
    }

}
