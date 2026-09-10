package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.service.service.user.CommonApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commonApi")
@Slf4j
public class CommonApiController {

    @Autowired
    private CommonApiService commonApiService;

    /**
     * 历史上的今天事件接口
     * @return json
     */
    @GetMapping("/historyNews")
    public Result<String> historyNew() {
        log.info("historyNew");
        return Result.success(commonApiService.historyNews());
    }

    /**
     * 地区新闻
     * @param areaName 地区名称
     * @return json
     */
    @GetMapping("/areaNews")
    public Result<String> areaNews(String areaName) {
        log.info("areaNews: {}", areaName);
        return Result.success(commonApiService.areaNews(areaName));
    }
}
