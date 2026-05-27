package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.pojo.entity.Category;
import com.jxcia.blog.mapper.user.CategoryMapper;
import com.jxcia.blog.service.service.user.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类 serviceImpl
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取文章分类
     *
     * @return 文章分类列表
     */
    @Override
    public List<Category> list() {
        return categoryMapper.get();
    }
}
