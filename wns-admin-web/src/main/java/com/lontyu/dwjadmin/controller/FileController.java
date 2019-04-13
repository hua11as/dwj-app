package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @decription: 文件controller
 * @author: as
 * @date: 2018/10/21 21:04
 */
@RestController
@RequestMapping("/file")
@Slf4j
@Api(value = "FileController", tags = "文件控制器")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public R upload(@RequestParam("file") MultipartFile file) {
        String path;
        try {
            path = fileService.upload(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new RRException("上传文件失败");
        }

        return R.ok().put("path", path);
    }
}
