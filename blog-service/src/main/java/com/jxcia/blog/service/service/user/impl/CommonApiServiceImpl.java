package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.service.service.user.CommonApiService;
import com.jxcia.blog.service.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class CommonApiServiceImpl implements CommonApiService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private HttpUtil httpUtil;

    private static final String HISTORY_KEY = "historyNews:";

    /**
     * 历史上的今天接口
     * @return json
     */
    @Override
    public String historyNews() {
        // 读取 redis
        String cached = redisTemplate.opsForValue().get(HISTORY_KEY + LocalDate.now());
        if (cached != null) return cached;

        // 重新查询
        String newsJson = httpUtil.getTodayHistoryNews();
        // 放入 redis 24小时过期
        redisTemplate.opsForValue().set(HISTORY_KEY + LocalDate.now(), newsJson, 24, TimeUnit.HOURS);
        return newsJson;
    }
}
