package com.markcha.ems.config;

import org.junit.jupiter.api.Test;

class CrawlerTest {

    @Test
    void crawlInfo() {
        Crawler crawler = new Crawler();
        System.out.println(crawler.beginCrawl());
    }

}
