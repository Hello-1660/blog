package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.UserPageDto;
import com.jxcia.blog.pojo.vo.UserPageVo;

public interface UserManageService {
    PageResult<UserPageVo> list(UserPageDto dto);
    void toggleStatus(Integer id);
    void delete(Integer id);
    UserPageVo detail(Integer id);
}
