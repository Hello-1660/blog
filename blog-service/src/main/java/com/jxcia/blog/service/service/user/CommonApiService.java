package com.jxcia.blog.service.service.user;

public interface CommonApiService {

    /**
     * 里是上的今天事件
     * @return json
     */
    String historyNews();

    /**
     * 地区新闻
     * @param areaName 地区
     * @return json
     */
    String areaNews(String areaName);
}
