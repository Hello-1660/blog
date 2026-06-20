package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.mapper.user.CategoryMapper;
import com.jxcia.blog.pojo.dto.CategoryDto;
import com.jxcia.blog.pojo.entity.Category;
import com.jxcia.blog.service.service.admin.CategoryManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryManageServiceImpl implements CategoryManageService {
    private static final String CACHE_KEY = "category:list";

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Category> list() {
        return categoryMapper.get();
    }

    @Override
    public void save(CategoryDto dto) {
        Category existing = categoryMapper.getByName(dto.getName());
        if (existing != null) throw new UserException("分类名称已存在");
        Category category = new Category();
        category.setName(dto.getName());
        categoryMapper.insert(category);
        clearCache();
    }

    @Override
    public void update(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        categoryMapper.update(category);
        clearCache();
    }

    @Override
    public void delete(Integer id) {
        categoryMapper.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        try {
            redisTemplate.delete(CACHE_KEY);
        } catch (Exception ignored) {}
    }
}
