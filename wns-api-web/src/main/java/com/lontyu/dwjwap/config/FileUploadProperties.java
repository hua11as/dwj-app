package com.lontyu.dwjwap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @decription: 文件上传配置
 * @author: as
 * @date: 2018/10/21 22:05
 */
@ConfigurationProperties(prefix = "file.upload")
@Data
@Component
public class FileUploadProperties {
    // 上传目录
    private String uploadDir;

    // 下载地址
    private String downPath;

    // 二维码目录
    private String qrcodePath;
}
