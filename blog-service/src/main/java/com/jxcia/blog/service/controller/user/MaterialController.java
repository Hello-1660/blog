package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.OssExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.OssException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Material;
import com.jxcia.blog.service.service.user.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/material")
@Slf4j
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    /**
     * 用户上传素材
     * @return 素材地址
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Material material) {
        log.info("save material: {}", material);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        material.setUserId(userId);
        return Result.success(materialService.save(material));
    }
}
