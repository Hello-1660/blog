package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.IdentifyAssignDto;
import com.jxcia.blog.pojo.vo.UserIdentifyVo;

import java.util.List;

public interface IdentifyManageService {
    List<UserIdentifyVo> list();
    UserIdentifyVo getUserIdentify(Integer userId);
    void assign(IdentifyAssignDto dto);
    void remove(Integer userId);
}
