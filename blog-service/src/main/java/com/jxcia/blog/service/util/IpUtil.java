package com.jxcia.blog.service.util;

import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.service.Config;

/**
 * ip 工具类
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final Ip2Region IP_2_REGION;

    static {
        try {// 1, 创建 v4 的配置：指定缓存策略和 v4 的 xdb 文件路径
            final Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                    // .setXdbInputStream(InputStream)      // 设置 v4 xdb 文件的 inputstream 对象
                    // .setXdbFile(File)                    // 设置 v4 xdb File 对象
                    // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                    .setXdbInputStream(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("ip2region_v4.xdb")
                    )    // 设置 v4 xdb 文件的路径
                    .asV4();    // 指定为 v4 配置


            // 2, 创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径
            final Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)     // 指定缓存策略: NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                    // .setXdbInputStream(InputStream)      // 设置 v6 xdb 文件的 inputstream 对象
                    // .setXdbFile(File)                    // 设置 v6 xdb File 对象
                    // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                    .setXdbInputStream(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("ip2region_v6.xdb")
                    )    // 设置 v6 xdb 文件的路径
                    .asV6();    // 指定为 v6 配置

            IP_2_REGION = Ip2Region.create(v4Config, v6Config);
        } catch (Exception e) {
            throw new RuntimeException("初始化 ip2region 失败", e);
        }
    }


    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        // 处理 Nginx 代理
        ip = request.getHeader("x-forwarded-for");
        if (isValid(ip)) {
            // 多级代理，取第一个
            int index = ip.indexOf(',');
            if (index != -1) ip = ip.substring(0, index);
            return ip.trim();
        }

        // 处理 Nginx 常用头
        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) return ip.trim();

        // 处理直连 ip
        return request.getRemoteAddr();
    }

    /**
     * 返回 ip 所在位置
     * @param ip ip地址
     * @return 位置
     */
    public static String ip2Region(String ip)  {
        try {
            return IP_2_REGION.search(ip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 判断 ip 是否有效
     * @param ip ip
     * @return 是否有效
     */
    private static boolean isValid(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
