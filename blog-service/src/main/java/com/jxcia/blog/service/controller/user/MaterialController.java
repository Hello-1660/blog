package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Material;
import com.jxcia.blog.service.service.user.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
@Slf4j
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    /**
     * 用户上传素材
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody Material material) {
        log.info("save material: {}", material);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        material.setUserId(userId);
        materialService.save(material);
        return Result.success();
    }

    /**
     * 批量删除素材
     * @param ids 素材编号列表
     * @return 无
     */
    public Result<Void> delete(@RequestBody List<Integer> ids) {
        log.info("delete material: {}", ids);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        materialService.delete(userId, ids);
        return Result.success();
    }
}
