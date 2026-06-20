package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("select * from category")
    List<Category> get();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into category (name) values (#{name})")
    void insert(Category category);

    @Update("update category set name = #{name} where id = #{id}")
    void update(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from category where name = #{name}")
    Category getByName(String name);
}
