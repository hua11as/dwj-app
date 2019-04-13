package com.lontyu.dwjadmin.service.impl;

import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.property.FileUploadProperties;
import com.lontyu.dwjadmin.service.FileService;
import com.lontyu.dwjadmin.util.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 23:36
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    public String upload(InputStream is, String fileName) {
        if (null == is) {
            throw new RRException("上传文件流不能为空");
        }

        // 获取文件类型
        String fileType = this.getFileType(fileName);
        String date = DateTime.now().toString("yyyyMMdd");
        String targetDir = fileUploadProperties.getUploadDir() + date;
        String uploadFileName = UUID.randomUUID() + "." + fileType;
        boolean rs = FileUtils.upload(is, targetDir, uploadFileName);
        if (!rs) {
            throw new RRException("上传文件失败");
        }

        return fileUploadProperties.getDownPath() + date + "/" + uploadFileName;
    }

    private String getFileType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new RRException("获取文件类型失败：文件名位空");
        }

        String[] s = fileName.split("\\.");
        if (s.length < 2) {
            throw new RRException("获取文件类型失败：文件名不可用");
        }

        return s[s.length - 1];
    }
}
