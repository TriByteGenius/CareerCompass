package com.tribytegenius.CareerCompass.JobService.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class PageFetchService {

    public String fetchVisibleText(String url) {
        try {
            // Fetch the page and extract visible text for LLM analysis
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                    .timeout(12000)
                    .get();
            return doc.body() != null ? doc.body().text() : doc.text();
        } catch (Exception e) {
            // Fallback: return empty to let upper layer decide
            return "";
        }
    }
}


