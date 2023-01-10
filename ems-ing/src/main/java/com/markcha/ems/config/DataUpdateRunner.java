package com.markcha.ems.config;

import com.markcha.ems.mapper.updateSql.UpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DataUpdateRunner implements CommandLineRunner {
    @Autowired
    private UpdateMapper updateMapper;
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        updateMapper.updateStateComponent();
        updateMapper.updateMainPageComponent();
        updateMapper.updateDetailPageComponent();
        updateMapper.updateImportantComponent();
//        updateMapper.updateGroupPageComponent();
        updateMapper.deleteTagSetMapperDuplicate();
        updateMapper.deleteSroucesHourWithDay();
        updateMapper.insertSroucesHourWithDay();
    }
}
