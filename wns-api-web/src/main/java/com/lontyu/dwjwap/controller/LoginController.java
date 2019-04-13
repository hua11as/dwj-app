package com.lontyu.dwjwap.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.lontyu.dwjwap.config.SystemContext;
import com.lontyu.dwjwap.constants.RedisKeysEnum;
import com.lontyu.dwjwap.dao.VipMemberMapper;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.req.LoginReqVO;
import com.lontyu.dwjwap.dto.resp.LoginRespVO;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.service.SendSmsService;
import com.lontyu.dwjwap.utils.CookieUtil;
import com.lontyu.dwjwap.utils.NumberUtils;
import com.lontyu.dwjwap.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @decription: 登录控制器
 * @author: as
 * @date: 2018/10/22 23:21
 */
@Controller
@RequestMapping("/login")
@Slf4j
@Api(value = "LoginController", tags = "登录控制器")
public class LoginController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private SendSmsService sendSmsService;

    @PostMapping("/sendVerifyCode/{mobile}")
    @ResponseBody
    @ApiOperation("发送验证码")
    public BaseResponse sendVerifyCode(@PathVariable("mobile") String mobile) {
        log.info("开始发送登录验证码：mobile={}短信验证码...", mobile);

        VipMember vipMember = vipMemberMapper.getVipMemberByMobile(mobile);
        if (null == vipMember) {
            return BaseResponse.buildFail(BaseResponse.FAIL_CODE, "请先注册账号！");
        }

        String verifyCode = NumberUtils.create6Num();
        redisUtil.set(RedisKeysEnum.LOGIN_VERIFY_CODE.getKey() + ":" + mobile, verifyCode, RedisKeysEnum.LOGIN_VERIFY_CODE.getExpireIn());

        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("大玩家");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_149415718");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        Map<String, String> param = new HashMap<>(5);
        param.put("code", verifyCode);
        request.setTemplateParam(JSONObject.toJSONString(param));
        sendSmsService.sendSms(request);
        log.info("完成发送登录验证码：mobile={}短信验证码：verifyCode={}...", mobile, verifyCode);
        return BaseResponse.buildSuccess(verifyCode);
    }

    @PostMapping("/authentication")
    @ResponseBody
    @ApiOperation("登录")
    public BaseResponse authentication(@RequestBody LoginReqVO reqVO, HttpServletResponse response) {
        if (StringUtils.isBlank(reqVO.getMobile()) || StringUtils.isBlank(reqVO.getVerifyCode())) {
            return BaseResponse.buildFail(BaseResponse.FAIL_CODE, "登录失败！");
        }

        String key = RedisKeysEnum.LOGIN_VERIFY_CODE.getKey() + ":" + reqVO.getMobile();
        String verifyCode = (String) redisUtil.get(key);
        if (!reqVO.getVerifyCode().equals(verifyCode)) {
            return BaseResponse.buildFail(BaseResponse.FAIL_CODE, "登录失败！");
        }

        VipMember vipMember = vipMemberMapper.getVipMemberByMobile(reqVO.getMobile());
        if (null == vipMember) {
            return BaseResponse.buildFail(BaseResponse.FAIL_CODE, "请先注册账号！");
        }
        if (1 == vipMember.getStatus()) {
            return BaseResponse.buildFail(BaseResponse.FAIL_CODE, "您已被禁止登录！");
        }

        WechatMember wechatMember = wechatMemberMapper.selectByVipId(vipMember.getId());
        SystemContext.setCurrentUser(wechatMember.getId());
        String cookie = CookieUtil.setCookie("dwjCookie", wechatMember.getId() + "_dwj", true);

        LoginRespVO respVO = new LoginRespVO();
        respVO.setUserId(wechatMember.getId());
        respVO.setToken(cookie);
        return BaseResponse.buildSuccess(respVO);
    }
}
