package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.IdentifyAssignDto;
import com.jxcia.blog.pojo.dto.IdentifyDto;
import com.jxcia.blog.pojo.vo.UserIdentifyVo;

import java.util.List;

public interface IdentifyManageService {
    List<UserIdentifyVo> list();
    void save(IdentifyDto dto);
    void update(IdentifyDto dto);
    void delete(Integer id);
    UserIdentifyVo getUserIdentify(Integer userId);
    void assign(IdentifyAssignDto dto);
    void remove(Integer userId);
}
