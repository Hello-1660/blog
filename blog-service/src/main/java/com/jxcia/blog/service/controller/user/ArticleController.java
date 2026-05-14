package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
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
    @GetMapping("/search")
    public Result<PageResult<Article>> search(@RequestBody ArticleSearchDto articleDto) {
        log.info("article search: {}", articleDto);

        return Result.success(articleService.search(articleDto));
    }

    /**
     * 推荐文章详情列表
     * @return 推荐文章列表
     */
    @GetMapping("/detail")
    public Result<List<HotArticleVo>> hotDetail() {
        log.info("article hotDetail");

        return Result.success(articleService.hotDetail());
    }

    /**
     * 新增文章
     * @param articleDto 保存文章信息
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid ArticleDto articleDto) {
        log.info("article save: {}", articleDto);

        articleService.save(articleDto);

        return Result.success();
    }

    /**
     * 删除文章
     * @param articleId 文章编号
     * @return 无
     */
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
    @GetMapping("/browse/{id}")
    public Result<Article> browse (@PathVariable @NotNull Integer id) {
        log.info("article browse: {}", id);

        return Result.success(articleService.getById(id));
    }

    /**
     * 更新文章
     * @param article 更新文章信息
     * @return 文章信息
     */
    @PostMapping("/update")
    public Result<Article> update(@RequestBody Article article) {
        log.info("article update: {}", article);

        return Result.success(articleService.update(article));
    }
}
