package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.service.service.admin.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单 controller
 */
@RestController
@RequestMapping("/menu")
@Slf4j
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 新增菜单
     * @param menu 菜单信息
     * @return 无
     */
    public Result<Void> save(@RequestBody Menu menu) {
        log.info("新增菜单");
        menuService.save(menu);
        return Result.success();
    }
}
