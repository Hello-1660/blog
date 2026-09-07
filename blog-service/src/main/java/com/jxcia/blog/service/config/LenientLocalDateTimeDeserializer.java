package com.jxcia.blog.service.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 宽松的 LocalDateTime 反序列化器：客户端把 ""（空字符串）当作"不筛选"时，
 * 直接反序列化为 null，而不是抛异常导致整个请求失败。
 * 非空字符串仍按 ISO 格式（yyyy-MM-ddTHH:mm:ss）解析，与默认行为一致。
 */
public class LenientLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}
