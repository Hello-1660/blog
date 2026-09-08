package com.jxcia.blog.service.controller.test;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.TestMessage;
import com.jxcia.blog.service.service.test.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    /**
     * 获取测试信息
     * @return 测试对象
     */
    @GetMapping
    public Result<TestMessage> test() {
        log.info("获取测试信息");
        return Result.success(testService.test());
    }
}
