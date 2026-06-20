package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.mapper.user.ArticleBrowseLogMapper;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.mapper.user.ReportMapper;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.vo.DashboardVo;
import com.jxcia.blog.service.service.admin.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private ArticleBrowseLogMapper articleBrowseLogMapper;

    @Override
    public DashboardVo getStats() {
        return DashboardVo.builder()
                .totalUsers(userMapper.countTotal())
                .activeUsers(userMapper.countByStatus(1))
                .disabledUsers(userMapper.countByStatus(0))
                .totalArticles(articleMapper.countTotal())
                .publicArticles(articleMapper.countByStatus(1))
                .privateArticles(articleMapper.countByStatus(0))
                .bannedArticles(articleMapper.countByStatus(2))
                .totalReports(reportMapper.countTotal())
                .pendingReports(reportMapper.countByStatus(0))
                .handledReports(reportMapper.countByStatus(1))
                .totalPageViews(articleBrowseLogMapper.countTotal())
                .categoryDistribution(articleMapper.countByCategory())
                .monthlyArticleTrend(articleMapper.countByMonth())
                .build();
    }
}
