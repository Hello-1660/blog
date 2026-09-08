package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.MaterialExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.MaterialException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.MaterialFolderDto;
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
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody List<Integer> ids) {
        log.info("delete material: {}", ids);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        materialService.delete(userId, ids);
        return Result.success();
    }

    /**
     * 创建素材文件夹
     * @param materialFolderDto 素材文件夹
     * @return 无
     */
    @PostMapping("createFolder")
    public Result<Void> createFolder(@RequestBody MaterialFolderDto materialFolderDto) {
        log.info("create folder: {}", materialFolderDto);
        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (materialFolderDto.getName().isEmpty()) throw new MaterialException(MaterialExceptionConstant.NAME_IS_NULL);
        if (materialFolderDto.getType() == null) throw new MaterialException(MaterialExceptionConstant.TYPE_IS_NULL);

        materialService.createFolder(materialFolderDto, userId);

        return Result.success();
    }

    /**
     * 用户更新素材文件夹
     * @param materialFolderDto 素材文件夹
     * @return 无
     */
    @PostMapping("folderUpdate")
    public Result<Void> folderUpdate(@RequestBody MaterialFolderDto materialFolderDto) {
        log.info("update folder: {}", materialFolderDto);
        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (materialFolderDto.getName().isEmpty()) throw new MaterialException(MaterialExceptionConstant.NAME_IS_NULL);
        if (materialFolderDto.getType() == null) throw new MaterialException(MaterialExceptionConstant.TYPE_IS_NULL);

        materialService.updateFolder(materialFolderDto, userId);

        return Result.success();
    }

    /**
     * 获取文件夹内素材详情
     * @param id 文件夹编号
     * @return 素材列表
     */
    @GetMapping("/folderDetail/{id}")
    public Result<List<Material>> folderDetail(@PathVariable Integer id) {
        log.info("folder detail: {}", id);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (id == null) throw new MaterialException(MaterialExceptionConstant.MATERIAL_IS_NULL);

        return Result.success(materialService.folderDetail(id, userId));
    }
}
