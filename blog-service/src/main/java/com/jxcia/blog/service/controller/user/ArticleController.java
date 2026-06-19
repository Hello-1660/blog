package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthOptional;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.dto.ArticleUpdateDto;
import com.jxcia.blog.pojo.vo.ArticleMsgVo;
import com.jxcia.blog.pojo.vo.ArticleSearchVo;
import com.jxcia.blog.pojo.vo.ArticleVo;
import com.jxcia.blog.pojo.vo.HotArticleVo;
import com.jxcia.blog.service.service.user.ArticleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章 controller
 */
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 文章搜索
     * @param articleDto 文章搜索信息
     * @return 文章分数据
     */
    @Anonymous
    @PostMapping("/search")
    public Result<PageResult<ArticleSearchVo>> search(@RequestBody ArticleSearchDto articleDto) {
        log.info("article search: {}", articleDto);

        return Result.success(articleService.search(articleDto));
    }

    /**
     * 推荐文章详情列表
     * @return 推荐文章列表
     */
    @Anonymous
    @GetMapping("/detail")
    public Result<List<HotArticleVo>> hotDetail() {
        log.info("article hotDetail");

        return Result.success(articleService.hotDetail());
    }

    /**
     * 关注用户的文章列表
     * @return 文章列表
     */
    @AuthRequired
    @GetMapping("/followed")
    public Result<List<HotArticleVo>> followed() {
        log.info("article followed");
        return Result.success(articleService.followedArticles());
    }

    /**
     * 新增文章
     * @param articleDto 保存文章信息
     * @return 文章编号
     */
    @AuthRequired
    @PostMapping("/save")
    public Result<Integer> save(@RequestBody @Valid ArticleDto articleDto) {
        log.info("article save: {}", articleDto);

        return Result.success(articleService.save(articleDto));
    }

    /**
     * 删除文章
     * @param articleId 文章编号
     * @return 无
     */
    @AuthRequired
    @DeleteMapping("/delete")
    public Result<Void> delete(@NotNull Integer articleId) {
        log.info("article delete: {}", articleId);

        articleService.delete(articleId);

        return Result.success();
    }

    /**
     * 查看文章
     * @param id 文章编号
     * @return 文章
     */
    @AuthOptional
    @GetMapping("/browse/{id}")
    public Result<ArticleVo> browse (@PathVariable @NotNull Integer id) {
        log.info("article browse: {}", id);

        return Result.success(articleService.getById(id));
    }

    /**
     * 更新文章
     * @param articleUpdateDto 更新文章信息
     */
    @AuthRequired
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid ArticleUpdateDto articleUpdateDto) {
        log.info("article update: {}", articleUpdateDto);

        articleService.update(articleUpdateDto);

        return Result.success();
    }

    /**
     * 查询文章互动信息
     * @param id 文章编号
     * @return 文章信息
     */
    @Anonymous
    @GetMapping("/articleMsg/{id}")
    public Result<ArticleMsgVo> articleMsg(@PathVariable @NotNull Integer id) {
        log.info("article articleMsg: {}", id);
        return Result.success(articleService.articleMsg(id));
    }
}
