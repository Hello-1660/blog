package com.jxcia.blog.service.service;

import com.aliyun.oss.OSS;
import com.jxcia.blog.service.config.OssProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jxcia.blog.common.exception.OssException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class OssService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final OSS oss;
    private final OssProperties properties;

    public OssService(OSS oss, OssProperties properties) {
        this.oss = oss;
        this.properties = properties;
    }

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new OssException("不支持的文件格式: " + extension);
        }

        String objectName = DATE_FMT.format(LocalDate.now()) + "/" + UUID.randomUUID() + "." + extension;

        try {
            oss.putObject(properties.getBucketName(), objectName, file.getInputStream());
        } catch (IOException e) {
            throw new OssException("上传失败: " + e.getMessage(), e);
        }

        return properties.getBaseUrl() + "/" + objectName;
    }

    public void deleteImage(String imageUrl) {
        String baseUrl = properties.getBaseUrl();
        if (imageUrl != null && imageUrl.startsWith(baseUrl + "/")) {
            String objectName = imageUrl.substring(baseUrl.length() + 1);
            oss.deleteObject(properties.getBucketName(), objectName);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
