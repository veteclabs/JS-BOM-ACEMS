package com.markcha.ems.config;

import com.markcha.ems.domain.Device;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CrawlerRunner implements CommandLineRunner, ApplicationListener {
    private static List<Timer> tasks = new ArrayList<>();
    @Autowired
    private DeviceDslRepositoryImpl deviceDslRepository;
    @Autowired
    private WebaccessApiServiceImpl webaccessApiService;
    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Crawler.class);
        List<Device> devices = deviceDslRepository.findAllDevices();

        for (Device device : devices) {
            Timer timer = new Timer();
            Crawler crawler = new Crawler(webaccessApiService, logger);
            crawler.setDevice(device);
            timer.schedule(crawler, 100, 100);
            tasks.add(timer);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        for (Timer task : tasks) {
            task.cancel();
        }
    }
}
