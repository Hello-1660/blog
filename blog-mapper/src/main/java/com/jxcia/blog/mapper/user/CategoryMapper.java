package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类 mapper
 */
@Mapper
public interface CategoryMapper {

    /**
     * 获取文章分类
     * @return
     */
    @Select("select * from category")
    List<Category> get();
}
