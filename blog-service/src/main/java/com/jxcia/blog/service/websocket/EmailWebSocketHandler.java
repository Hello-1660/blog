package com.jxcia.blog.service.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EmailWebSocketHandler extends TextWebSocketHandler {

    /** userId → session */
    private static final Map<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = getUserId(session);
        if (userId != null) {
            sessions.put(userId, session);
            log.info("WebSocket 连接: userId={}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            log.info("WebSocket 断开: userId={}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 前端发来的心跳，忽略
    }

    /**
     * 向指定用户推送新邮件通知
     */
    public void notifyNewEmail(Integer userId) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("new_email"));
            } catch (IOException e) {
                log.warn("WebSocket 推送失败: userId={}", userId);
            }
        }
    }

    private Integer getUserId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : "";
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && "userId".equals(kv[0])) {
                try { return Integer.parseInt(kv[1]); } catch (NumberFormatException e) { return null; }
            }
        }
        return null;
    }
}
