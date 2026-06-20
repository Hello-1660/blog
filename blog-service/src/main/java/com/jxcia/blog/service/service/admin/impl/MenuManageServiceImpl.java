package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.exception.AdminException;
import com.jxcia.blog.mapper.admin.MenuMapper;
import com.jxcia.blog.pojo.dto.MenuDto;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.vo.MenuTreeVo;
import com.jxcia.blog.service.service.admin.MenuManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuManageServiceImpl implements MenuManageService {
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<MenuTreeVo> tree() {
        List<Menu> all = menuMapper.getAll();
        List<MenuTreeVo> vos = all.stream().map(m -> {
            MenuTreeVo vo = new MenuTreeVo();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).collect(Collectors.toList());
        return buildTree(vos, 0);
    }

    private List<MenuTreeVo> buildTree(List<MenuTreeVo> all, Integer pId) {
        List<MenuTreeVo> tree = new ArrayList<>();
        for (MenuTreeVo vo : all) {
            if (vo.getPId() != null && vo.getPId().equals(pId)) {
                vo.setChildren(buildTree(all, vo.getId()));
                tree.add(vo);
            }
        }
        tree.sort(Comparator.comparing(MenuTreeVo::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        return tree;
    }

    @Override
    public void save(MenuDto dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        menu.setCreateTime(LocalDateTime.now());
        menuMapper.insert(menu);
    }

    @Override
    public void update(MenuDto dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        menuMapper.update(menu);
    }

    @Override
    public void delete(Integer id) {
        List<Menu> children = menuMapper.getChildrenByPid(id);
        if (!children.isEmpty()) throw new AdminException("请先删除子菜单");
        menuMapper.deleteById(id);
    }
}
