package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.dto.ArticleCollectionRelationDto;
import com.jxcia.blog.pojo.entity.ArticleCollection;
import com.jxcia.blog.pojo.entity.ArticleListCollection;
import com.jxcia.blog.pojo.vo.ArticleCollectionVo;
import com.jxcia.blog.pojo.vo.CollectionArticleListVo;
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
     * 获取用户自己的集合列表
     * @return 原始集合列表
     */
    @GetMapping("/collection")
    public Result<List<ArticleCollectionVo>> collectionList() {
        log.info("articleCollection selfCollectionList");

        // 获取
        Integer id = SecurityContextUtil.getId();
        if (id == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        return Result.success(articleCollectionService.collectionList(id));
    }

    /**
     * 获取用户自己的集合列表
     * @return 原始集合列表
     */
    @GetMapping("/collection/{id}")
    public Result<List<ArticleCollectionVo>> collectionList(@PathVariable Integer id) {
        log.info("articleCollection selfCollectionList id: {}", id);

        if (id == null) throw  new UserException(UserExceptionConstant.USER_NOT_EXISTS);

        return Result.success(articleCollectionService.collectionList(id));
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
    public Result<List<CollectionArticleListVo>> list(@PathVariable Integer id) {
        log.info("articleCollection list id:{}", id);

        if (id == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        return Result.success(articleCollectionService.list(id));
    }

    /**
     * 添加文章
     * @param articleCollectionRelationDto 添加文章信息
     * @return 无
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody ArticleCollectionRelationDto articleCollectionRelationDto) {
        log.info("articleCollection add id:{}", articleCollectionRelationDto);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null)
            throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (articleCollectionRelationDto.getArticleId() == null)
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        if (articleCollectionRelationDto.getCollectionId() == null)
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        articleCollectionService.add(userId, articleCollectionRelationDto);

        return Result.success();
    }

    /**
     * 删除文章集合中的文章
     * @param articleListCollection 文章集合信息
     * @return 无
     */
    @PostMapping("/remove")
    public Result<Void> remove(@RequestBody ArticleListCollection articleListCollection) {
        log.info("articleCollection remove articleListCollection:{}", articleListCollection);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        // 文章和集合编号不能为空
        if (articleListCollection.getCollectionId() == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);
        if (articleListCollection.getArticleIds() != null && !articleListCollection.getArticleIds().isEmpty())
            articleListCollection.getArticleIds().forEach(articleId -> {
                if (articleId == null) throw new ArticleException(ArticleExceptionConstant.ILLEGAL_OPERATION);
            });
        articleCollectionService.remove(userId, articleListCollection);

        return Result.success();
    }

    /**
     * 修改集合信息
     * @param articleCollection 集合信息
     * @return 无
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody ArticleCollection articleCollection) {
        log.info("articleCollection update articleCollectionDto:{}", articleCollection);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null)
            throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (articleCollection.getId() == null)
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);
        if (articleCollection.getName() == null || articleCollection.getName().trim().isEmpty())
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NAME_IS_NULL);

        articleCollectionService.update(userId, articleCollection);

        return Result.success();
    }

    /**
     * 删除文章集合
     * @param id 文章集合编号
     * @return 无
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        log.info("articleCollection delete id:{}", id);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (id == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        articleCollectionService.delete(userId, id);

        return Result.success();
    }
}
