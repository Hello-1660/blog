package com.jxcia.blog.blog.security.service;

import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;
import java.util.Map;

public interface DynamicSecurityService {
    Map<String, Collection<ConfigAttribute>> loadDataSource();
}
