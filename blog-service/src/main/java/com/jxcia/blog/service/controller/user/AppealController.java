package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.AppealDto;
import com.jxcia.blog.service.service.user.AppealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appeal")
@Slf4j
public class AppealController {
    @Autowired
    private AppealService appealService;

    /**
     * 申诉
     * @param appealDto 申诉内容
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody AppealDto appealDto) {
        log.info("appealDto:{}", appealDto);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);
        if (appealDto.getType() == null) throw new UserException(UserExceptionConstant.APPEAL_TYPE_NULL);
        if (appealDto.getObjectId() == null) throw new UserException(UserExceptionConstant.APPEAL_OBJECT_NULL);

        appealDto.setUserId(userId);
        appealService.save(appealDto);
        return Result.success();
    }
}
