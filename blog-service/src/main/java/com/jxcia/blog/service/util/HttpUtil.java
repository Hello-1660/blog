package com.jxcia.blog.service.util;

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
import java.util.List;

@Component
@Slf4j
public class HttpUtil {
    @Value("${wanwei.api-key}")
    private static String apiKey = "";

    public static void getTodayHistoryNews() throws InterruptedException, IOException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://route.showapi.com/119-42?appKey=" + apiKey))
                    .POST(BodyPublishers.ofString("needContent=1"))
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 处理响应
            if (response.statusCode() == 200) {
                String body = response.body();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        getTodayHistoryNews();
    }
}
