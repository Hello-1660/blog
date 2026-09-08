package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.common.constant.MaterialExceptionConstant;
import com.jxcia.blog.common.exception.MaterialException;
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
        List<Material> materialList = materialMapper.getByIds(ids);

        // 防止越权
        materialList.forEach(m -> {
            if (!m.getUserId().equals(userId))
                throw new MaterialException(MaterialExceptionConstant.CANNOT_DEL_OTHER_MATERIAL);
        });

        materialMapper.deleteByIds(userId, ids);
        //删除 oss 文件
        materialList.forEach(m -> ossService.deleteImage(m.getUrl()));
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

    /**
     * 用户跟新素材文件夹
     * @param materialFolderDto 素材文件夹
     * @param userId 用户编号
     */
    @Override
    public void updateFolder(MaterialFolderDto materialFolderDto, Integer userId) {
        materialFolderMapper.updateById(materialFolderDto, userId);
    }

    /**
     * 文件夹素材
     * @param id 文件夹编号
     * @param userId 用户编号
     * @return 素材列表
     */
    @Override
    public List<Material> folderDetail(Integer id, Integer userId) {
        return materialMapper.getByFolderId(id, userId);
    }

    /**
     * 获取素材文件夹
     * @param folderId 素材文件夹编号
     * @return 素材文件夹
     */
    public MaterialFolder getFolder(Integer folderId) {
        return materialMapper.getById(folderId);
    }
}
