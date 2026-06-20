package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.CategoryDto;
import com.jxcia.blog.pojo.entity.Category;

import java.util.List;

public interface CategoryManageService {
    List<Category> list();
    void save(CategoryDto dto);
    void update(CategoryDto dto);
    void delete(Integer id);
}
