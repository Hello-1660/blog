package com.jxcia.blog.service.service.test.impl;

import com.jxcia.blog.pojo.entity.TestMessage;
import com.jxcia.blog.service.mapper.test.TestMapper;
import com.jxcia.blog.service.service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestMapper testMapper;

    /**
     * 获取测试信息
     * @return 返回测试信息对象
     */
    @Override
    public TestMessage test() {
        return testMapper.get();
    }
}
