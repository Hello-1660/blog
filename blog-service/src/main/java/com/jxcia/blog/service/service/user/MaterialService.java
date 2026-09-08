package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.MaterialFolderDto;
import com.jxcia.blog.pojo.entity.Material;

import java.util.List;

public interface MaterialService {
    /**
     * 用户上传素材
     * @param material 素材信息
     * @return 无
     */
    void save(Material material);

    /**
     * 用户批量删除素材
     * @param ids 素材编号列表
     */
    void delete(Integer userId, List<Integer> ids);

    /**
     * 用户创建素材文件夹
     * @param materialFolderDto 素材文件夹
     */
    void createFolder(MaterialFolderDto materialFolderDto, Integer userId);

    /**
     * 用户更新素材文件夹
     * @param materialFolderDto 素材文件夹
     * @param userId 用户编号
     */
    void updateFolder(MaterialFolderDto materialFolderDto, Integer userId);

    /**
     * 获取文件夹素材列表
     * @param id 文件夹编号
     * @param userId 用户编号
     * @return 素材列表
     */
    List<Material> folderDetail(Integer id, Integer userId);

    /**
     * 删除素材文件夹
     * @param id 文件夹编号
     * @param userId 用户编号
     */
    void folderDelete(Integer id, Integer userId);

    /**
     * 更新素材
     * @param material 素材
     */
    void update(Material material, Integer userId);
}
