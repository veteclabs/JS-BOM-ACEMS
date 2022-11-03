package com.markcha.ems.config;
import com.markcha.ems.domain.Device;
import com.markcha.ems.dto.tag.TagDto;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Setter
@Getter
public class Crawler extends TimerTask {
    private Device device;

    private long startTime;

    private HashMap<String, Object> result;

    public void setResult(String k, Object v) {
        this.result.put(k, v);
    }

    public void initResult() {
        this.result = new HashMap<String, Object>();
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
                    List<String> tagList = device.getEquipment().getTagLists().stream()
                            .map(t -> t.getNickname())
                            .collect(toList());
                    getInfo_v2(driver, tagList);

                    // sout -> 크롤링 정상 작동 테스트용 메서드
                    System.out.println(getResult());

                    sleep(5000);
                }
            }
        }
    }

    private void chkStartStatus(String loading) {
        if(loading.contains("INITIALISATION")) sysOut("실행 중");
    }

    private void sysOut(String s) {
        System.out.printf("[크롤러-%s]: %s %n", device.getEquipment().getModel(), s);
    }

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        WebDriver driver = new ChromeDriver(options);

        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "C:/workspace-2022/JS-BOM-ACEMS/ems-ing/chromedriver.exe";
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        String url = "http://" + device.getSerialNumber() + "/";
        driver.get(url);
        return driver;
    }

    private void getInfo_v2(WebDriver driver, List<String> tagLists) {
        initResult();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> trList = driver.findElements(By.xpath("//tr"));

        for (WebElement tr : trList) {
            String target = tr.findElement(By.xpath("./td[1]")).getText();

            if (tagLists.contains(target)) {
                Object val = tr.findElement(By.xpath("./td[2]")).getText();

                if (target.equals("Machine Status")) {
                    if (val.equals("Load")) { val = 1; }
                    else if (val.equals("Unload")) { val = 0; }
                }
                // setResult -> 크롤링 정상 작동 여부 테스트용 메서드
                this.setResult(target, val);
                TagDto.builder()
                        .tagName(target)
                        .value(val)
                        .build();
            }
        }
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

}