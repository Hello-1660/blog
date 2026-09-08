package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.mapper.user.MaterialFolderMapper;
import com.jxcia.blog.mapper.user.MaterialMapper;
import com.jxcia.blog.pojo.dto.MaterialFolderDto;
import com.jxcia.blog.pojo.entity.Material;
import com.jxcia.blog.pojo.entity.MaterialFolder;
import com.jxcia.blog.service.service.OssService;
import com.jxcia.blog.service.service.user.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private MaterialFolderMapper materialFolderMapper;
    @Autowired
    private OssService ossService;

    /**
     * 用户上传素材
     * @param material 素材信息
     * @return 无
     */
    @Override
    public void save(Material material) {
        material.setUploadTime(LocalDateTime.now());
        materialMapper.insert(material);

    }

    /**
     * 批量删除素材
     * @param userId 用户编号
     * @param ids 素材编号列表
     */
    @Override
    public void delete(Integer userId, List<Integer> ids) {
        materialMapper.deleteByIds(userId, ids);
        // TODO 删除 oss 文件
    }

    /**
     * 用户创建素材文件夹
     * @param materialFolderDto 素材文件夹
     * @param userId 用户编号
     */
    @Override
    public void createFolder(MaterialFolderDto materialFolderDto, Integer userId) {
        MaterialFolder folder = MaterialFolder.builder()
                .type(materialFolderDto.getType())
                .name(materialFolderDto.getName())
                .userId(userId)
                .createTime(LocalDateTime.now())
                .build();

        materialFolderMapper.insert(folder);
    }
}
