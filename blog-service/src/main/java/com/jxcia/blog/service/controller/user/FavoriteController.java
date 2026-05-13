package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.service.service.user.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收藏夹 controller
 */
@RestController
@RequestMapping("/favorite")
@Slf4j
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    /**
     * 新增收藏夹
     * @param favoriteDto 收藏夹
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody FavoriteDto favoriteDto) {
        log.info("save favorite: {}", favoriteDto);

        favoriteService.save(favoriteDto);

        return Result.success();
    }
}
