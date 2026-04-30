package com.jxcia.blog.service.mapper.admin;

import com.jxcia.blog.pojo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("select * from permission")
    List<Permission> getAll();
}
