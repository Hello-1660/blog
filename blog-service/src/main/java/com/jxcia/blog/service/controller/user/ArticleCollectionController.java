package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.ArticleCollectionVo;
import com.jxcia.blog.service.service.user.ArticleCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articleCollection")
@Slf4j
public class ArticleCollectionController {
    @Autowired
    private ArticleCollectionService articleCollectionService;

    /**
     * 获取集合信息
     * @param id 集合编号
     * @return 集合信息
     */
    @GetMapping("/detail/{id}")
    public Result<ArticleCollectionVo> detail(@PathVariable Integer id) {
        log.info("articleCollection detail id:{}", id);

        if (id == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        return Result.success(articleCollectionService.detail(id));
    }

    /**
     * 创建文章集合
     * @param articleCollectionDto 文章集合信息
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody ArticleCollectionDto articleCollectionDto) {
        log.info("articleCollection save articleCollectionDto:{}", articleCollectionDto);

        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        if (articleCollectionDto.getName() == null || articleCollectionDto.getName().trim().isEmpty())
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NAME_IS_NULL);

        articleCollectionService.save(userId, articleCollectionDto);

        return Result.success();
    }

    /**
     * 获取文章集合中的文章
     * @param id 文章集合编号
     * @return 文章列表
     */
    @GetMapping("/list/{id}")
    public Result<List<Article>> list(@PathVariable Integer id) {
        log.info("articleCollection list id:{}", id);

        if (id == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        return Result.success(articleCollectionService.list(id));
    }
}
