package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.entity.Category;

import java.util.List;

/**
 * 分类服务
 */
public interface CategoryService {

    /**
     * 获取文章分类
     * @return 文章分类列表
     */
    List<Category> list();
}
