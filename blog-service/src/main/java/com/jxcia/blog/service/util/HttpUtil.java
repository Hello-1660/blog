package com.jxcia.blog.service.util;

import com.jxcia.blog.common.exception.CommonApiException;
import com.jxcia.blog.pojo.entity.TodayHistoryNews;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class HttpUtil {
    @Value("${wanwei.api-key}")
    private String apiKey;

    public String getTodayHistoryNews() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://route.showapi.com/119-42"))
                    .POST(BodyPublishers.ofString("needContent=1"))
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setHeader("appKey", apiKey)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 处理响应
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new CommonApiException(response.statusCode() + "");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonApiException(e.getMessage());
        }
    }

    public String getAreaNews(String areaName) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://route.showapi.com/170-47"))
                    .POST(BodyPublishers.ofString("title=&areaName=" + areaName + "&page=1&areaId="))
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setHeader("appKey", apiKey)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 处理响应
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new CommonApiException(response.statusCode() + "");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonApiException(e.getMessage());
        }
    }
}
