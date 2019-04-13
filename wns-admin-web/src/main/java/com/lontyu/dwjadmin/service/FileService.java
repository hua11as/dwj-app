package com.lontyu.dwjadmin.service;

import java.io.InputStream;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 23:36
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param is       上传文件流
     * @param fileName 原始文件名称
     * @return 文件访问地址
     */
    String upload(InputStream is, String fileName);
}
