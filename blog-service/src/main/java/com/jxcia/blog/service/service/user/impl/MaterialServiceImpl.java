package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.mapper.user.MaterialMapper;
import com.jxcia.blog.pojo.entity.Material;
import com.jxcia.blog.service.service.user.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MaterialServiceImpl implements MaterialService {
    @Autowired
    private MaterialMapper materialMapper;
    /**
     * 用户上传素材
     * @param material 素材信息
     * @return 素材地址
     */
    @Override
    public String save(Material material) {
        material.setUploadTime(LocalDateTime.now());
        materialMapper.insert(material);
    }
}
