package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.MenuDto;
import com.jxcia.blog.pojo.vo.MenuTreeVo;

import java.util.List;

public interface MenuManageService {
    List<MenuTreeVo> tree();
    void save(MenuDto dto);
    void update(MenuDto dto);
    void delete(Integer id);
}
