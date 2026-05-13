package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Favorite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    /**
     * 插入收藏夹记录
     * @param favorite 收藏夹
     */
    @Insert("insert into favorite (user_id, name, create_time, status) " +
            "value (#{userId}, #{name}, #{createTime}, #{status})")
    void insert(Favorite favorite);

    /**
     * 根据收藏夹信息查询收藏夹记录
     * @param favoriteDto 收藏夹信息
     * @return 收藏夹记录
     */
    @Select("select * from favorite where name = #{name}")
    List<Favorite> getByFavoriteDto(FavoriteDto favoriteDto);
}
