package com.jxcia.blog.service.service.user.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxcia.blog.pojo.entity.Category;
import com.jxcia.blog.mapper.user.CategoryMapper;
import com.jxcia.blog.service.service.user.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String CACHE_KEY = "category:list";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Category> list() {
        // 1. 尝试从 Redis 读取缓存
        try {
            String cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<Category>>() {});
            }
        } catch (Exception e) {
            log.warn("读取分类缓存失败，回源数据库", e);
        }

        // 2. 缓存未命中，查数据库
        List<Category> categories = categoryMapper.get();

        // 3. 写入 Redis 缓存（1 小时 TTL）
        try {
            String json = objectMapper.writeValueAsString(categories);
            redisTemplate.opsForValue().set(CACHE_KEY, json, CACHE_TTL);
        } catch (Exception e) {
            log.warn("写入分类缓存失败", e);
        }

        return categories;
    }
}
