package com.markcha.ems.config;

import com.markcha.ems.domain.Device;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class CrawlerRunner implements CommandLineRunner {
    @Autowired
    private DeviceDslRepositoryImpl deviceDslRepository;
    @Autowired
    private WebaccessApiServiceImpl webaccessApiService;

    @Override
    public void run(String... args) {
        int delay = 0;

        Logger logger = LoggerFactory.getLogger(Crawler.class);
        List<Device> devices = deviceDslRepository.findAllDevices();
        downloadDriver();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(devices.size());

        for (Device device : devices) {
            try {
                Runnable runnable = () -> {
                    Crawler crawler = new Crawler(webaccessApiService, logger);
                    crawler.setDevice(device);
                    try {
                        crawler.crawl();
                    } catch (Exception e) {
                        // driver.findElement(By.xpath("//td")).getText(); -> 예외 발생 -> crawl을 재호출해야하나
                        // 크롤링을 위해 생성한 크롬 브라우저 프로세스만 죽일 방법이 없음
                        System.out.println("[크롤러 스레드 꺼짐 상태]: " + executor.isShutdown());
                       e.printStackTrace();
                    }
                };
                executor.submit(runnable);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void downloadDriver() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        String chromeDriver = System.getProperty("user.dir") + "/ChromeDriverInstaller/ChromeDriverInstaller.exe";

        try {
            pr = rt.exec(chromeDriver);
            pr.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
