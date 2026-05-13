package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.FavoriteExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.FavoriteException;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.service.mapper.user.FavoriteMapper;
import com.jxcia.blog.service.service.user.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏夹 serviceImpl
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 新增收藏夹
     *
     * @param favoriteDto 收藏夹
     */
    @Override
    public void save(FavoriteDto favoriteDto) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        if (favoriteDto.getName() == null || favoriteDto.getName().isEmpty()) {
            throw new FavoriteException(FavoriteExceptionConstant.NAME_CANNOT_NULL);
        }

        List<Favorite> f = favoriteMapper.getByFavoriteDto(favoriteDto);
        if (!f.isEmpty()) throw new FavoriteException(FavoriteExceptionConstant.NAME_CANNOT_EQUALS);

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .name(favoriteDto.getName())
                .createTime(LocalDateTime.now())
                .status(favoriteDto.getStatus())
                .build();

        favoriteMapper.insert(favorite);
    }
}
