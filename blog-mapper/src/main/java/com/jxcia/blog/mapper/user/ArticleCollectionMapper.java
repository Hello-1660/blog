package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.ArticleCollectionRelationDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.ArticleCollection;
import com.jxcia.blog.pojo.entity.ArticleCollectionRelation;
import com.jxcia.blog.pojo.entity.ArticleListCollection;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCollectionMapper {
    /**
     * 根据文章集合编号查询文章集合
     * @param id 文章集合编号
     * @return 文章集合
     */
    @Select("select * from article_collection where id = #{id}")
    ArticleCollection getById(Integer id);

    /**
     * 插入文章集合
     * @param articleCollection 文章集合
     */
    @Insert("insert into article_collection (name, user_id, sort, create_time) " +
            "value (#{name}, #{userId}, #{sort}, #{createTime})")
    void insert(ArticleCollection articleCollection);

    /**
     * 根据集合编号查询文章列表
     * @param id 集合编号
     * @return 文章列表
     */
    @Select("select * from article where id in " +
            "(select article_id from article_collection_relation where collection_id = #{id})")
    List<Article> getArticleListByCollectionId(Integer id);

    /**
     * 想文章集合关系表插入数据
     * @param articleCollectionRelation 文章集合关系
     */
    @Insert("insert into article_collection_relation (article_id, collection_id, add_time) " +
            "value (#{articleId}, #{collectionId}, #{addTime})")
    void insertACRByACR(ArticleCollectionRelation articleCollectionRelation);

    /**
     * 根据文章编号和集合编号查询文章关系
     * @param articleCollectionRelationDto 文章编号和集合编号
     * @return 文章关系记录
     */
    @Select("select * from article_collection_relation where collection_id = #{collectionId} and article_id = #{articleId}")
    ArticleCollectionRelation getACRByArticleIdAndCollectionId(ArticleCollectionRelationDto articleCollectionRelationDto);

    /**
     * 删除文章集合中的文章
     * @param articleListCollection 文章集合信息
     */
    void removeACRByACRList(ArticleListCollection articleListCollection);

    /**
     * 根据文章编号删除文章集合中的文章
     * @param id 文章编号
     */
    @Delete("delete from article_collection_relation where article_id = #{id}")
    void removeACRByArticleId(Integer id);
}
