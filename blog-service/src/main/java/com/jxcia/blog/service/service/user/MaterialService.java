package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.entity.Material;

public interface MaterialService {
    /**
     * 用户上传素材
     * @param material 素材信息
     * @return 素材地址
     */
    String save(Material material);
}
