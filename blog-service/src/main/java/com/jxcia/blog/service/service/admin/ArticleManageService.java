package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ArticlePageDto;
import com.jxcia.blog.pojo.vo.ArticlePageVo;

public interface ArticleManageService {
    PageResult<ArticlePageVo> list(ArticlePageDto dto);
    void toggleStatus(Integer id);
    void delete(Integer id);
}
