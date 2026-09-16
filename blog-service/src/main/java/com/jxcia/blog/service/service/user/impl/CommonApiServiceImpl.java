package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.common.constant.RedisExceptionConstant;
import com.jxcia.blog.common.exception.RedisException;
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
    private static final String AREA_NEWS_KEY = "areaNews:";

    /**
     * 历史上的今天接口
     * @return json
     */
    @Override
    public String historyNews() {
        try {
            // 读取 redis
            String cached = redisTemplate.opsForValue().get(HISTORY_KEY + LocalDate.now());
            if (cached != null) return cached;

            // 重新查询
            String newsJson = httpUtil.getTodayHistoryNews();
            // 放入 redis 24小时过期
            redisTemplate.opsForValue().set(HISTORY_KEY + LocalDate.now(), newsJson, 24, TimeUnit.HOURS);
            return newsJson;
        } catch (Exception e) {
            throw new RedisException(RedisExceptionConstant.REDIS_EXCEPTION);
        }
    }

    /**
     * 地区新闻
     * @param areaName 地区
     * @return json
     */
    @Override
    public String areaNews(String areaName) {
        // 读取 redis
        String cached = redisTemplate.opsForValue().get(AREA_NEWS_KEY + areaName);
        if (cached != null) return cached;

        // 重新查询
        String newsJson = httpUtil.getAreaNews(areaName);
        // 放入 redis 24小时过期
        redisTemplate.opsForValue().set(AREA_NEWS_KEY + areaName, newsJson, 24, TimeUnit.HOURS);
        return newsJson;
    }
}
