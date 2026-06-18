package com.jxcia.blog.service.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.CommentExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.exception.CommentException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.pojo.entity.LikeComment;
import com.jxcia.blog.pojo.vo.CommentMsgVo;
import com.jxcia.blog.pojo.vo.CommentWithUserVo;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.mapper.user.CommentMapper;
import com.jxcia.blog.mapper.user.UserLikeCommentMapper;
import com.jxcia.blog.service.service.user.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论 serviceImpl
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserLikeCommentMapper userLikeCommentMapper;

    /**
     * 添加用户评论
     * @param commentDto 用户评论信息
     * @return 新建的评论及用户信息
     */
    @Override
    public CommentWithUserVo save(CommentDto commentDto) {
        Integer userId = SecurityContextUtil.getId();

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setUserId(userId);
        comment.setSort(0);
        comment.setCreateTime(LocalDateTime.now());

        commentMapper.insert(comment);

        CommentWithUserVo vo = commentMapper.getCommentWithUserVoById(comment.getId());
        vo.setLikeNum(0);
        vo.setIsLiked(false);
        return vo;
    }

    /**
     * 删除用户评论
     *
     * @param commentId 评论编号
     */
    @Transactional
    @Override
    public void delete(Long commentId) {
        Integer userId = SecurityContextUtil.getId();

        Comment comment = commentMapper.get(commentId);
        if (comment == null) throw new CommentException(CommentExceptionConstant.COMMENT_NOT_FOND);
        Article article = articleMapper.getById(comment.getArticleId());

        // 只有作者和本人可以删除评论
        if (article.getUserId().equals(userId) || comment.getUserId().equals(userId)) {
            // 收集所有后代评论 ID
            List<Long> descendantIds = new ArrayList<>();
            collectDescendants(commentId, descendantIds);

            // 删除所有后代的点赞信息
            for (Long did : descendantIds) {
                userLikeCommentMapper.deleteByCommentId(did);
            }
            // 删除所有后代评论
            for (Long did : descendantIds) {
                commentMapper.delete(did);
            }

            // 删除自己的点赞信息
            userLikeCommentMapper.deleteByCommentId(commentId);
            // 删除评论
            commentMapper.delete(commentId);
        } else {
            throw new CommentException(CommentExceptionConstant.OTHER_USER_CANNOT_DEL_COMMENT);
        }
    }

    /**
     * 递归收集所有后代评论 ID
     */
    private void collectDescendants(Long parentId, List<Long> result) {
        List<Long> childIds = commentMapper.getIdsByFId(parentId);
        for (Long childId : childIds) {
            result.add(childId);
            collectDescendants(childId, result);
        }
    }

    /**
     * 作者置顶/取消置顶评论
     *
     * @param commentId 评论编号
     * @return true 为已置顶，false 为已取消
     */
    @Override
    public boolean pin(Long commentId) {
        Integer userId = SecurityContextUtil.getId();
        Comment comment = commentMapper.get(commentId);
        if (comment == null) throw new CommentException(CommentExceptionConstant.COMMENT_NOT_FOND);

        Article article = articleMapper.getById(comment.getArticleId());
        if (!article.getUserId().equals(userId))
            throw new CommentException(CommentExceptionConstant.OTHER_USER_CANNOT_DEL_COMMENT);

        if (comment.getSort() != null && comment.getSort() > 0) {
            commentMapper.updateSort(commentId, 0);
            return false;
        } else {
            commentMapper.updateSort(commentId, 1);
            return true;
        }
    }

    /**
     * 用户点赞评论
     *
     * @param commentId 用户评论编号
     */
    @Override
    public void like(Long commentId) {
        Integer userId = SecurityContextUtil.getId();

        Comment comment = commentMapper.get(commentId);
        if (comment == null) throw new CommentException(CommentExceptionConstant.COMMENT_NOT_FOND);

        LikeComment likeComment = LikeComment.builder()
                .userCommentId(comment.getId())
                .userId(userId)
                .build();

        // 查询点赞记录
        LikeComment lc = userLikeCommentMapper.getByUserLikeComment(likeComment);

        // 用户已经点赞过就取消点赞，没有点赞过就添加点赞信息
        if (lc != null) {
            userLikeCommentMapper.delete(lc.getId());
        } else {
            LikeComment blc = LikeComment.builder().
                    userId(userId)
                    .userCommentId(commentId)
                    .createTime(LocalDateTime.now())
                    .build();
            userLikeCommentMapper.insert(blc);
        }
    }

    /**
     * 分页查看文章评论
     *
     * @param articleId 文章编号
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 文章评论分页数据
     */
    @Override
    public PageResult<CommentWithUserVo> detail(Integer articleId, Integer pageNum, Integer pageSize) {
        if (articleId == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        Integer userId = SecurityContextUtil.getId();

        PageHelper.startPage(pageNum, pageSize);
        List<CommentWithUserVo> cvoList = commentMapper.getCommentWithUserVoByArticleId(articleId);

        if (!cvoList.isEmpty()) {
            List<Long> cIdList = cvoList.stream().map(CommentWithUserVo::getId).toList();
            List<CommentMsgVo> cmvList = commentMapper.getCommentLikeNumByCommentIds(cIdList, userId);
            Map<Long, CommentMsgVo> cmvMap = cmvList.stream().collect(Collectors.toMap(CommentMsgVo::getId, c -> c));

            cvoList.forEach(comment -> {
                CommentMsgVo msg = cmvMap.get(comment.getId());
                if (msg != null) {
                    comment.setLikeNum(msg.getLikeNum());
                    comment.setIsLiked(msg.getIsLiked() == 1);
                } else {
                    comment.setLikeNum(0);
                    comment.setIsLiked(false);
                }
            });
        }

        PageInfo<CommentWithUserVo> pageInfo = new PageInfo<>(cvoList);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}
