package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;
import com.jxcia.blog.service.service.user.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 删除收藏夹
     * @param favoriteId 收藏夹编号
     * @return 无
     */
    @DeleteMapping("/delete/{favoriteId}")
    public Result<Void> delete(@PathVariable Long favoriteId) {
        log.info("delete favorite: {}", favoriteId);

        favoriteService.delete(favoriteId);

        return Result.success();
    }

    /**
     * 收藏夹文章编号
     * @param favoriteArticle 收藏夹文章信息
     * @return 无
     */
    @PostMapping("/addArticle")
    public Result<Void> addArticle(@RequestBody FavoriteArticle favoriteArticle) {
        log.info("addArticle favorite: {}", favoriteArticle);

        favoriteService.addArticle(favoriteArticle);

        return Result.success();
    }

    /**
     * 更新收藏夹
     * @param favorite 收藏夹信息
     * @return 收藏夹
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Favorite favorite) {
        log.info("update favorite: {}", favorite);

        favoriteService.update(favorite);

        return Result.success();
    }

    /**
     * 查看收藏夹列表
     * @return 收藏夹列表
     */
    @GetMapping("/list")
    public Result<List<Favorite>> list() {
        log.info("favorite list");

        return Result.success(favoriteService.favoriteList());
    }
}
