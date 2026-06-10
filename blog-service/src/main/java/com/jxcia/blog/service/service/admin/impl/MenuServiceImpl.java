package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.mapper.admin.MenuMapper;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.service.service.admin.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 菜单 serviceImpl
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     */
    @Override
    public void save(Menu menu) {
        menuMapper.insert(menu);
    }
}
