package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;
import com.jxcia.blog.service.service.user.FavoriteService;
import jakarta.validation.Valid;
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
    @AuthRequired
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid FavoriteDto favoriteDto) {
        log.info("save favorite: {}", favoriteDto);

        favoriteService.save(favoriteDto);

        return Result.success();
    }

    /**
     * 删除收藏夹
     * @param favoriteId 收藏夹编号
     * @return 无
     */
    @AuthRequired
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
    @AuthRequired
    @PostMapping("/addArticle")
    public Result<Void> addArticle(@RequestBody @Valid FavoriteArticle favoriteArticle) {
        log.info("addArticle favorite: {}", favoriteArticle);

        favoriteService.addArticle(favoriteArticle);

        return Result.success();
    }

    /**
     * 更新收藏夹
     * @param favorite 收藏夹信息
     * @return 收藏夹
     */
    @AuthRequired
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
    @AuthRequired
    @GetMapping({"/list/{id}", "/list"})
    public Result<List<Favorite>> list(@PathVariable(required = false) Integer id) {
        log.info("favorite list: {}", id);

        return Result.success(favoriteService.favoriteList(id));
    }

    /**
     * 获取收藏夹文章列表
     * @param favoriteId 收藏夹编号
     * @return 文章列表
     */
    @AuthRequired
    @GetMapping("/listArticle/{favoriteId}")
    public Result<List<Article>> listArticle(@PathVariable Integer favoriteId) {
        log.info("favorite listArticle: {}", favoriteId);

        return Result.success(favoriteService.listArticle(favoriteId));
    }

    /**
     * 移除收藏夹中的文章
     * @param favoriteId 收藏夹编号
     * @param articleId 文章编号
     * @return 无
     */
    @AuthRequired
    @DeleteMapping("/removeArticle")
    public Result<Void> removeArticle(Long favoriteId, Integer articleId) {
        log.info("removeArticle favorite: {} article: {}", favoriteId, articleId);

        favoriteService.removeArticle(favoriteId, articleId);

        return Result.success();
    }

    /**
     * 移除收藏夹中所有文章
     * @param favoriteId 收藏夹编号
     * @return 无
     */
    @AuthRequired
    @DeleteMapping("/removeAllArticles/{favoriteId}")
    public Result<Void> removeAllArticles(@PathVariable Long favoriteId) {
        log.info("removeAllArticles favorite: {}", favoriteId);

        favoriteService.removeAllArticles(favoriteId);

        return Result.success();
    }
}
