package com.lontyu.dwjadmin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 21:14
 */
@Slf4j
public class FileUtils {

    public static boolean upload(InputStream is, String targetDir, String fileName) {
        // 参数校验
        if (null == is || StringUtils.isBlank(targetDir) || StringUtils.isBlank(fileName)) {
            return false;
        }

        // 生成文件目录
        File file = new File(targetDir);
        if (!file.exists()) {
            boolean mkdirRs = file.mkdirs();
            if (!mkdirRs) {
                return false;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(targetDir + "/" + fileName)) {
            byte[] buffer = new byte[1024];
            int size;
            while ((size = is.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
        } catch (IOException e) {
            log.error("file upload error:", e);
            return false;
        }
        return true;
    }
}
