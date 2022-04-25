package com.markcha.ems.component;

import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final DeviceDslRepositoryImpl deviceDslRepository;
    @Scheduled(fixedDelay = 1000)
    public void alarmFixedRateTask() {

    }
}
