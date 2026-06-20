package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.ArticleSearchVo;
import com.jxcia.blog.pojo.vo.HotArticleVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章 mapper
 */
@Mapper
public interface ArticleMapper {

    /**
     * 根据 articleDto 分页查询文章
     * @param articleDto 查询文章信息
     * @return 文章列表
     */
    List<ArticleSearchVo> getByArticleDto(ArticleSearchDto articleDto);

    /**
     * 插入文章
     * @param article 文章
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into article (user_id, icon, title, content, create_time, update_time, sort, status, category_id) " +
            "value (#{userId}, #{icon}, #{title}, #{content}, #{createTime}, #{updateTime}, #{sort}, #{status}, #{categoryId})")
    void insert(Article article);

    /**
     * 根据用户编号查询文章
     * @param userId 用户编号
     * @return 文章列表
     */
    @Select("select * from article where user_id = #{userId} order by create_time desc")
    List<Article> getByUserId(Integer userId);

    /**
     * 根据文章编号列表查询文章
     * @param articleIdList 文章编号列表
     * @return 文章列表
     */
    List<Article> getByArticleIds(List<Integer> articleIdList);

    /**
     * 根据文章编号删除文章
     * @param articleId 文章编号
     */
    @Delete("delete from article where id = #{articleId}")
    void deleteByArticleId(Integer articleId);

    /**
     * 根据用户编号查询文章
     * @param articleId 文章编号
     * @return 文章
     */
    @Select("select * from article where id = #{articleId}")
    Article getByArticleId(Integer articleId);

    /**
     * 根据文章搜索信息查询推荐文章
     * @return 推荐文章列表
     */
    List<HotArticleVo> getHotArticleByArticleSearchDto();

    /**
     * 获取关注用户的文章列表
     * @param userId 当前用户编号
     * @return 文章列表
     */
    @Select("select a.id, a.user_id, u.nickname as userNickname, a.icon, a.title, a.create_time, 0 as value, a.category_id " +
            "from article a join user u on a.user_id = u.id " +
            "join subscribe s on a.user_id = s.sub_user_id " +
            "where s.user_id = #{userId} and a.status = 1 " +
            "order by a.create_time desc")
    List<HotArticleVo> getFollowedArticles(Integer userId);

    /**
     * 根据文章编号查询文章
     * @param id 文章编号
     * @return 文章
     */
    @Select("select * from article where id = #{id}")
    Article getById(Integer id);

    void update(Article article);

    List<com.jxcia.blog.pojo.vo.ArticlePageVo> getPage(com.jxcia.blog.pojo.dto.ArticlePageDto dto);

    Integer count(com.jxcia.blog.pojo.dto.ArticlePageDto dto);

    @Select("select count(*) from article")
    Integer countTotal();

    @Select("select count(*) from article where status = #{status}")
    Integer countByStatus(Integer status);

    List<java.util.Map<String, Object>> countByCategory();

    @Select("select date_format(create_time, '%Y-%m') as month, count(*) as count from article " +
            "where create_time >= date_sub(now(), interval 6 month) group by month order by month")
    List<java.util.Map<String, Object>> countByMonth();
}
