package com.jxcia.blog.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库连接健康检查定时任务
 * 无用户访问期间（如夜间），定期执行轻量查询，
 * 防止 MySQL 因 wait_timeout 断开空闲连接。
 */
@Slf4j
@Component
public class DatabaseHealthCheck {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthCheck(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 每 30 秒执行一次心跳查询，保持连接活跃
     */
    @Scheduled(fixedRate = 30_000)
    public void keepConnectionAlive() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.trace("数据库心跳检查成功");
        } catch (Exception e) {
            log.warn("数据库心跳检查失败: {}", e.getMessage());
        }
    }
}
