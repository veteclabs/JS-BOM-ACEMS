package com.markcha.ems.config;

import com.markcha.ems.domain.Device;
import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class CrawlerRunner implements CommandLineRunner {
    @Autowired
    private DeviceDslRepositoryImpl deviceDslRepository;
    @Autowired
    private WebaccessApiServiceImpl webaccessApiService;

    @Override
    public void run(String... args) throws Exception {
        int delay = 0;

        Logger logger = LoggerFactory.getLogger(Crawler.class);
        List<Device> devices = deviceDslRepository.findAllDevices();
        downloadDriver();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(devices.size());

        for (Device device : devices) {
            Runnable runnable = () -> {
                Crawler crawler = new Crawler(webaccessApiService, logger);
                crawler.setDevice(device);
                try {
                    crawler.crawl();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            executor.schedule(runnable, delay, TimeUnit.SECONDS);
        }
    }

    private void downloadDriver() {
        Logger logger = LoggerFactory.getLogger(CrawlerRunner.class);
        try {
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
            logger.info("chromedriver.exe process has been killed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String versionFullName = takeChromeVersion();
        String majorVersionName = versionFullName.split("\\.")[0];
        String fileUrl = String.format("https://storage.googleapis.com/chrome-for-testing-public/%s/win64/chromedriver-win64.zip", versionFullName);
        String outputFileName = "chromedriver.zip";

        String destDir = "chromedriver";
        Path path = Paths.get("./" + destDir);
        if (!Files.exists(path)) { // 폴더가 존재하지 않으면
            try {
                Files.createDirectories(path); // 폴더 생성
                logger.debug("Directory created: " + path.toString());
            } catch (Exception e) {
                e.printStackTrace(); // 예외 처리
            }
        } else {
            logger.debug("Directory already exists: " + path.toString());
        }
        while (true) {
            try {
                downloadFile(fileUrl, outputFileName, destDir);
                logger.info("Download completed successfully.");
                break;
            } catch (IOException e) {
                versionFullName = decrementVersion(versionFullName);
                fileUrl = String.format("https://storage.googleapis.com/chrome-for-testing-public/%s/win64/chromedriver-win64.zip", versionFullName);
                logger.error("Error occurred during download: " + e.getMessage());
            }
        }
        try {
            unzip(outputFileName, destDir);
            logger.info("Extraction completed successfully.");
        } catch (IOException e) {
            logger.info("Error occurred during extraction: " + e.getMessage());
        }

    }
    private String decrementVersion(String version) {
        String[] parts = version.split("\\.");
        int lastPart = Integer.parseInt(parts[parts.length - 1]); // 마지막 부분을 정수로 변환

        lastPart--; // 마지막 부분을 감소
        parts[parts.length - 1] = String.valueOf(lastPart); // 감소된 값을 다시 문자열로 변환

        return String.join(".", parts); // 변경된 부분을 다시 문자열로 합쳐서 반환
    }
    public static void downloadFile(String fileUrl, String outputFileName, String dirPath) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream( outputFileName)) {

            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
    private String takeChromeVersion() {
        String version = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", "reg query \"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\" /v version"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("version")) {
                    // 라인에서 버전 정보만 추출
                    return line.split("\\s+")[line.split("\\s+").length - 1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void unzip(String zipFilePath, String destDir) throws IOException {
        String fileZip = System.getProperty("user.dir") + "\\" + zipFilePath;
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(zipEntry.getName());
                // 디렉토리인 경우 디렉토리 생성
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // 파일을 추출하기 전에 상위 디렉토리가 존재하는지 확인하고, 없다면 생성
                    File parent = newFile.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }

                    // 파일 추출
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        System.out.println(filePath);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\" +filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

}