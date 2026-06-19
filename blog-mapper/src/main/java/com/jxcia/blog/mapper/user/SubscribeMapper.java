package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Subscribe;
import com.jxcia.blog.pojo.vo.SubscribeVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubscribeMapper {

    /**
     * 根据用户编号和关注用户编号来查询用户关注记录
     * @param subscribe 用户关注记录
     * @return 用户关注记录
     */
    @Select("select * from subscribe where user_id = #{userId} and sub_user_id = #{subUserId}")
    Subscribe getBySubscribe(Subscribe subscribe);

    /**
     * 插入用户关注记录
     * @param subscribe 用户关注记录
     */
    @Insert("insert into subscribe (user_id, sub_user_id, sort, create_time)" +
            "value (#{userId}, #{subUserId}, #{sort}, #{createTime})")
    void insert(Subscribe subscribe);

    /**
     * 根据用户编号和关注用户编号删除用户记录
     * @param ss 用户记录
     */
    @Delete("delete from subscribe where user_id = #{userId} and sub_user_id = #{subUserId}")
    void deleteBySubscribe(Subscribe ss);

    /**
     * 根据用户编号查询用户关注记录
     * @param userId 用户编号
     * @return 关注列表
     */
    @Select("select " +
            "s.id as id, s.sub_user_id as subUserId, u.nickname as nickname, u.icon as icon, s.sort as sort, s.create_time as createTime " +
            "from subscribe s " +
            "inner join user u on s.sub_user_id = u.id and u.account_status = 1 " +
            "where s.user_id = #{userId}")
    List<SubscribeVo> getSubscribeVoByUserId(Integer userId);

    /**
     * 根据关注用户编号查询用户关注记录
     * @param subscribeId 关注用户编号
     * @return 粉丝列表
     */
    @Select("select " +
            "s.id as id, s.sub_user_id as subUserId, u.nickname as nickname, u.icon as icon, s.sort as sort, s.create_time as createTime " +
            "from subscribe s " +
            "inner join user u on s.user_id = u.id and u.account_status = 1 " +
            "where s.sub_user_id = #{subscribeId}")
    List<SubscribeVo> getSubscribeVoBySubscribeId(Integer subscribeId);

    /**
     * 查询粉丝编号列表
     * @param userId 用户编号
     * @return 粉丝编号列表
     */
    @Select("select user_id from subscribe where sub_user_id = #{userId}")
    List<Integer> getUserIdListBySubscribeId(Integer userId);

    /**
     * 获取关注数量
     * @param id 用户编号
     * @return 用户关注数量
     */
    @Select("select count(*) from subscribe where user_id = #{id}")
    Integer getSubscribeNumberByUserId(Integer id);

    /**
     * 获取粉丝数量
     * @param id 关注用户编号
     * @return 粉丝数量
     */
    @Select("select count(*) from subscribe where sub_user_id = #{id}")
    Integer getUserNumberBySubUserId(Integer id);

    /**
     * 更新关注置顶状态
     * @param subscribe 关注记录
     */
    @org.apache.ibatis.annotations.Update("update subscribe set sort = #{sort} where id = #{id}")
    void updateSort(Subscribe subscribe);
}
