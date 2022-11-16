package com.markcha.ems.config;
import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Setter
@Getter
public class Crawler extends TimerTask {
    private Device device;
    private long startTime;
    private List<TagDto> tagDtoList;
    private WebaccessApiServiceImpl apiService;
    private Logger logger;
    public Crawler(WebaccessApiServiceImpl webaccessApiService,Logger logger) {
        apiService = webaccessApiService;
        this.logger = logger;
    }

    @Override
    public void run() {

        WebDriver driver = createDriver();

        startTime =  System.currentTimeMillis();

        while (true) {
            String loading = driver.findElement(By.xpath("//td")).getText();

            chkStartStatus(loading);

            chkAndRefresh(driver, startTime);

            sleep(2000);

            if (!loading.contains("INITIALISATION")) {
                while (true) {
                    tagDtoList = new ArrayList<>();
                    List<TagDto> info_v2 = getInfo_v2(driver);
                    apiService.setTagValues(info_v2);

                    sleep(5000);
                }
            }
        }
    }

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        String WEB_DRIVER_ID = "webdriver.chrome.driver";

        String WEB_DRIVER_PATH = System.getProperty("user.dir") + "/ChromeDriverInstaller/chromedriver.exe";

        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        WebDriver driver = new ChromeDriver(options);

        String url = device.getSerialNumber();
        driver.get(url);

        return driver;
    }

    private void chkStartStatus(String loading) {
        if(loading.contains("INITIALISATION")) sysOut("실행 중");
    }

    private void sysOut(String s) {
        String model = device.getEquipment().getModel();
        logger.info(String.format("[Crawler%13s]: %s", model, s));
    }

    // 공압기 정보 페이지 로딩 과정에서 발생하는 문제를 처리하는 메서드
    private void chkAndRefresh(WebDriver driver, long start) {
        long endTime = System.currentTimeMillis();
        long diff = (endTime - start) / 1000;

        // 로딩 중 에러가 발생하면 페이지를 새로고침한다.
        try {
            String errorBox = driver.findElement(By.xpath("//td[@id='step2comment']")).getCssValue("visibility");
            if (errorBox.equals("visible")) {
                sysOut("로딩 중 에러 발생 -> 새로고침");

                sleep(2000);
                driver.navigate().refresh();
                startTime =  System.currentTimeMillis();

            }
        } catch (Exception e) {
            sysOut("로딩 에러 없음 -> 크롤링 시작");
        }

        // 로딩 페이지가 20초 이상 지속되면 페이지를 새로고침하고 로딩 시작 시간을 초기화한다.
        if (diff >= 20) {
            driver.navigate().refresh();
            startTime =  System.currentTimeMillis();
            sysOut("대기 시간 만료 -> 새로고침");
        }
    }

    private void sleep(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<TagDto> getInfo_v2(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> trList = driver.findElements(By.xpath("//tr"));
        List<TagDto> tagDtos = new ArrayList<>();

        for (WebElement tr : trList) {
            String target = tr.findElement(By.xpath("./td[1]")).getText();
            Map<String, Tag> nickUnitSet = device.getTags().stream()
                    .collect(toMap(t -> t.getTagList().getNickname()+t.getTagList().getUnit(), v -> v, (p1, p2) -> p1));

            Map<String, Tag> nickSet = device.getTags().stream()
                    .collect(toMap(t -> t.getTagList().getNickname(), v -> v, (p1, p2) -> p1));

            if (nickSet.keySet().contains(target)) {
                Object val = tr.findElement(By.xpath("./td[2]")).getText();

                if (target.equals("Flow")) {
                    val = tr.findElement(By.xpath("./td[3]")).getText();
                }

                String unit;
                if (val.toString().split(" ").length >= 2) { unit = val.toString().split(" ")[1]; }
                else { unit = null; }

                if (nickUnitSet.keySet().contains(target+unit)) {
                    Tag tag = nickUnitSet.get(target + unit);

                    if (target.equals("Machine Status")) {
                        if (val.equals("Load") || val.equals("Stopped")) { val = new Integer(1); }
                        else if (val.equals("Unload")|| val.equals("Started")) { val = new Integer(0); }
                    }


                    try {
                        if (val.toString().trim().contains(" ")) {
                            if (val.toString().contains(".")) { val = new Double(val.toString().split(" ")[0]); }
                            else { val = Integer.parseInt(val.toString().split(" ")[0]); }
                        } else {
                            if (val.toString().contains(".")) { val = new Double(val.toString().split(" ")[0]); }
                            else if (isNumeric(val.toString())) { val = Integer.parseInt(val.toString().split(" ")[0]); }
                        }
                    } catch  (NumberFormatException n) {
                        driver.quit();
                        driver.close();
                    }

                    TagDto tagDto = TagDto.builder()
                            .tagName(tag.getTagName())
                            .value(val)
                            .build();

                    if(isNumeric(val.toString())) tagDtos.add(tagDto);
                }
            }

        }
        return tagDtos;
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}