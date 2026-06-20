package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVo {
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer disabledUsers;
    private Integer totalArticles;
    private Integer publicArticles;
    private Integer privateArticles;
    private Integer bannedArticles;
    private Integer totalReports;
    private Integer pendingReports;
    private Integer handledReports;
    private Integer totalPageViews;
    private List<Map<String, Object>> categoryDistribution;
    private List<Map<String, Object>> monthlyArticleTrend;
}
