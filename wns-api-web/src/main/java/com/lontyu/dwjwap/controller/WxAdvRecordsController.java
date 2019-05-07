package com.lontyu.dwjwap.controller;

import com.lontyu.dwjwap.dao.SpreadInfoMapper;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.entity.SysConfig;
import com.lontyu.dwjwap.service.SysConfigService;
import com.lontyu.dwjwap.service.WxAdvService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 微信推广
 */
@Controller
@RequestMapping("/adv")
@Api(value = "WxAdvRecordsController", tags = "微信推广")
public class WxAdvRecordsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SpreadInfoMapper spreadInfoMapper;

    @Autowired
    WxAdvService wxAdvService;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 推广页面
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping("/spread/{userId}")
    @ApiOperation("推广页面")
    public String spread(@PathVariable(value = "userId") Integer userId, Model model) {
        String url = spreadInfoMapper.selectUrlByUserId(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("url", url);
        return "page/pages/spread/spread";
    }

    /**
     * 自动生成 推广url 地址
     *
     * @param userId
     * @return
     */
    @GetMapping("/getAdvUrl")
    @ResponseBody
    @ApiOperation("获取推广url")
    public BaseResponse<String> getAdvUrl(@RequestParam("userId") Integer userId) {
        BaseResponse<String> response = BaseResponse.buildSuccess(null);
        logger.info("开始获取userId={]，的推广url 地址...", userId);
        String url = spreadInfoMapper.selectUrlByUserId(userId);
        if (url == null || url.equals("")) {
            logger.info("用户userId={},首次生成推广地址开始中...", userId);
            response = wxAdvService.createAdvRecords(userId);
        }
        logger.info("用户userId={},生成推广地址完成，response={}", userId, response);
        return response;
    }

    @PostMapping("getCustomerNameCard")
    @ApiOperation("获取客服名片")
    @ResponseBody
    public BaseResponse<String> getCustomerNameCard() {
        return BaseResponse.buildSuccess(Optional.ofNullable(
                sysConfigService.getSysConfigByParamKey("customerNameCard")).map(SysConfig::getParamValue).orElse(""));
    }
}
