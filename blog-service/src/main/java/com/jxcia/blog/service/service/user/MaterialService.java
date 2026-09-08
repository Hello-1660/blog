package com.jxcia.blog.service.service.user;

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
}
