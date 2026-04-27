package com.jxcia.blog.service.mapper.test;

import com.jxcia.blog.pojo.entity.TestMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestMapper {

    /**
     * 获取测试信息
     * @return 测试信息对象
     */
    @Select("select message from test")
    TestMessage get();
}
