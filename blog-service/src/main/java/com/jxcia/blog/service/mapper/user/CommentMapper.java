package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    @Insert("insert into user_comment (user_id, article_id, f_id, content, sort, create_time) " +
            "value (#{userId}, #{articleId}, #{fId}, #{content}, #{sort}, #{createTime})")
    void insert(Comment comment);
}
