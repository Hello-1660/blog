package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.ArticleCollection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleCollectionMapper {
    /**
     * 根据文章集合编号查询文章集合
     * @param id 文章集合编号
     * @return 文章集合
     */
    @Select("select * from article_collection where id = #{id}")
    ArticleCollection getById(Integer id);

    /**
     * 插入文章集合
     * @param articleCollection 文章集合
     */
    @Insert("insert into article_collection (name, user_id, sort, create_time) " +
            "value (#{name}, #{userId}, #{sort}, #{createTime})")
    void insert(ArticleCollection articleCollection);
}
