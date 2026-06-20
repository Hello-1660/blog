package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.exception.AdminException;
import com.jxcia.blog.mapper.user.IdentifyMapper;
import com.jxcia.blog.pojo.dto.IdentifyAssignDto;
import com.jxcia.blog.pojo.dto.IdentifyDto;
import com.jxcia.blog.pojo.entity.UserIdentify;
import com.jxcia.blog.pojo.vo.UserIdentifyVo;
import com.jxcia.blog.service.service.admin.IdentifyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentifyManageServiceImpl implements IdentifyManageService {
    @Autowired
    private IdentifyMapper identifyMapper;

    @Override
    public List<UserIdentifyVo> list() {
        return identifyMapper.getAll();
    }

    @Override
    public void save(IdentifyDto dto) {
        UserIdentify identify = new UserIdentify();
        identify.setName(dto.getName());
        identify.setDescription(dto.getDescription());
        identify.setType(dto.getType());
        identifyMapper.insert(identify);
    }

    @Override
    public void update(IdentifyDto dto) {
        UserIdentify identify = new UserIdentify();
        identify.setId(dto.getId());
        identify.setName(dto.getName());
        identify.setDescription(dto.getDescription());
        identify.setType(dto.getType());
        identifyMapper.update(identify);
    }

    @Override
    public void delete(Integer id) {
        identifyMapper.deleteById(id);
    }

    @Override
    public UserIdentifyVo getUserIdentify(Integer userId) {
        return identifyMapper.getIdentifyVoByUserId(userId);
    }

    @Override
    public void assign(IdentifyAssignDto dto) {
        UserIdentify identify = identifyMapper.getById(dto.getIdentifyId());
        if (identify == null) throw new AdminException("身份不存在");
        identifyMapper.deleteRelationByUserId(dto.getUserId());
        identifyMapper.insertRelation(dto.getUserId(), dto.getIdentifyId());
    }

    @Override
    public void remove(Integer userId) {
        identifyMapper.deleteRelationByUserId(userId);
    }
}
