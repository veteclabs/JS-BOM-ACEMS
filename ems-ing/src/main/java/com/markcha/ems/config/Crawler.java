package com.markcha.ems.config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Crawler {

    public List<String> attrs = new ArrayList<>(Arrays.asList(
//            =====================http://localhost:1099=====================
            "VSD 1-20% RPM",
            "VSD 20-40% RPM",
            "VSD 40-60% RPM",
            "VSD 60-80% RPM",
            "VSD 80-100% RPM",
//            =====================http://localhost:1100=====================
            "Compressor Outlet",
            "Element Outlet",
            "Ambient Air",
            "Running Hours",
            "Loaded Hours",
            "Motor Starts",
            "Load Relay",
            "Module Hours",
            "Machine Status",
            "Emergency Stop"
            ));

    private long startTime;

    public HashMap<String, Object> beginCrawl() {
        WebDriver driver = new ChromeDriver();
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "C:/workspace-2022/JS-BOM-ACEMS/ems-ing/chromedriver.exe";
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

//        String url = "http://localhost:1099/";
        String url = "http://localhost:1100/";

        driver.get(url);

        startTime =  System.currentTimeMillis();
        while (true) {
            chkAndRefresh(driver, startTime);

            sleep(2000);
            String loading = driver.findElement(By.xpath("//td")).getText();

            if (!loading.contains("INITIALISATION")) {
                return getInfo(driver);
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
                System.out.println("[크롤러]: 로딩 중 에러 발생 -> 새로고침");

                sleep(2000);
                driver.navigate().refresh();
                startTime =  System.currentTimeMillis();

            }
        } catch (Exception e) {
            System.out.println("[크롤러]: 로딩 에러 없음");
        }

        // 로딩 페이지가 20초 이상 지속되면 페이지를 새로고침하고 로딩 시작 시간을 초기화한다.
        if (diff >= 20) {
            driver.navigate().refresh();
            startTime =  System.currentTimeMillis();
            System.out.println("[크롤러]: 대기 시간 만료 -> 새로고침");
        }
    }

    private void sleep(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> getInfo(WebDriver driver) {
        HashMap<String, Object> result = new HashMap<>();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> trList = driver.findElements(By.xpath("//tr"));

        for (WebElement tr : trList) {
            String target = tr.findElement(By.xpath("./td[1]")).getText();

            if (attrs.contains(target)) {
                Object val = tr.findElement(By.xpath("./td[2]")).getText();

                if (target.equals("Machine Status")) {
                    if (val.equals("Load")) { val = 1; }
                    else if (val.equals("Unload")) { val = 0; }
                }

                result.put(target, val);
            }
        }
        return result;
    }
}