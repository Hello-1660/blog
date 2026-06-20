package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.dto.UserPageDto;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.pojo.vo.UserPageVo;
import com.jxcia.blog.service.service.admin.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManageServiceImpl implements UserManageService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<UserPageVo> list(UserPageDto dto) {
        com.github.pagehelper.PageHelper.startPage(dto.getPage(), dto.getSize());
        List<UserPageVo> list = userMapper.getPage(dto);
        long total = ((com.github.pagehelper.Page<UserPageVo>) list).getTotal();
        return new PageResult<>(total, list);
    }

    @Override
    public void toggleStatus(Integer id) {
        User user = userMapper.getEntityById(id);
        if (user == null) throw new UserException(UserExceptionConstant.USER_NOT_EXISTS);
        user.setAccountStatus(user.getAccountStatus() == 1 ? 0 : 1);
        userMapper.update(user);
    }

    @Override
    public void delete(Integer id) {
        userMapper.getEntityById(id);
        userMapper.deleteByIdList(List.of(id));
    }

    @Override
    public UserPageVo detail(Integer id) {
        User user = userMapper.getEntityById(id);
        if (user == null) throw new UserException(UserExceptionConstant.USER_NOT_EXISTS);
        return UserPageVo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .icon(user.getIcon())
                .accountStatus(user.getAccountStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}
