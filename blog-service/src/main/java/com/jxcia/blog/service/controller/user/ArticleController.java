package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.service.service.user.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
