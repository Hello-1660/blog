package com.jxcia.blog.common.constant;

public class EmailConstant {
    public static final int UNREAD = 0;
    public static final int READ = 1;

    public static String sendMail(String nickName, String articleName) {
        return "您关注的" + nickName + "发布了新作品" + "《" + articleName + "》，快来看看吧！";
    }
}
