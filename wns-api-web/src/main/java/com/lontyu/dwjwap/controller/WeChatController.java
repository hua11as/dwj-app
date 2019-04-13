package com.lontyu.dwjwap.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.google.gson.JsonObject;
import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.config.SystemContext;
import com.lontyu.dwjwap.constants.RedisKeysEnum;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.constants.TransTypeEnum;
import com.lontyu.dwjwap.dao.MoneyRecordMapper;
import com.lontyu.dwjwap.dao.VipMemberMapper;
import com.lontyu.dwjwap.dao.WechatConfigMapper;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.req.WxOrderReq;
import com.lontyu.dwjwap.entity.MoneyRecord;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.entity.WechatConfig;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.exception.BizException;
import com.lontyu.dwjwap.service.SendSmsService;
import com.lontyu.dwjwap.service.WeChatService;
import com.lontyu.dwjwap.dto.VipMemberVo;
import com.lontyu.dwjwap.utils.CookieUtil;
import com.lontyu.dwjwap.utils.NumberUtils;
import com.lontyu.dwjwap.utils.RedisUtil;
import com.lontyu.dwjwap.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能说明:微信接口控制器
 */
@Controller
@RequestMapping(value = "/weChat")
@Api(value = "WeChatController", tags = "微信接口")
public class WeChatController {

    private static Logger logger = LoggerFactory.getLogger(WeChatController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WechatConfigMapper wechatConfigMapper;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Autowired
    private GlobalsConfig globalsConfig;

    @Autowired
    private SendSmsService sendSmsService;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 获取appId
     *
     * @param configid
     * @return
     */
    @RequestMapping("/getAppId/{configid}")
    @ResponseBody
    @ApiOperation("获取appId")
    public JSONObject getAppId(@PathVariable("configid") Integer configid) {
        try {
            WechatConfig weChatConfig = wechatConfigMapper.selectByPrimaryKey(configid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("appId", weChatConfig.getAppId());
            return jsonObject;
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", -1);
            jsonObject.put("msg", "网络异常");
            return jsonObject;
        }
    }


    /**
     * 用户授权，记录账户信息
     *
     * @param request
     * @param code
     * @param configid
     * @param model
     * @return
     */
    @RequestMapping("/getOpenId/{configid}")
    @ApiOperation("获取openId")
    public String getOpenId(HttpServletRequest request, @RequestParam("code") String code, @PathVariable("configid") Integer configid, Model model) {
        logger.info("接收前端请求参数：code={},configId={},开始记录账户信息...", code, configid);
        WechatConfig weChatConfig = wechatConfigMapper.selectByPrimaryKey(configid);
        if (Objects.isNull(weChatConfig)) {
            logger.error("接收前端请求参数：code={},configId={},处理失败，configId 传入有误。", code, configid);
            return "page/pages/err";
        }
        String url = new StringBuilder(SystemConstant.WEIXIN_AUTH_URL).append("?appid=").append(weChatConfig.getAppId())
                .append("&secret=").append(weChatConfig.getAppSecret())
                .append("&grant_type=").append("authorization_code")
                .append("&code=").append(code).toString();
        String inviterId = request.getParameter("inviterId");
        String json = restTemplate.getForObject(url, String.class);

        logger.info("请求微信认证接口，请求地址：url={},返回结果：json={}, inviterId={}", url, json, inviterId);
        JSONObject obj = JSON.parseObject(json);
        String openid = obj.getString("openid");
        if (Objects.nonNull(openid)) {
            Map<String, String> params = new HashMap<>();
            params.put("openId", openid);
            params.put("wxConfigId", String.valueOf(configid));
            WechatMember weChatMember = wechatMemberMapper.getWeChatMemberByOpenId(params);

            // 获取微信用户参数信息
            String accessToken = weChatService.getWxAccessToken(weChatConfig.getAppId(), weChatConfig.getAppSecret(), configid);
            String nickName = null;
            String headImageUrl = null;
            if (Objects.nonNull(accessToken)) {
                String wxUserInfo = weChatService.getWxUserInfo(accessToken, openid);
                if (Objects.nonNull(wxUserInfo)) {
                    JSONObject userInfo = JSONObject.parseObject(wxUserInfo);
                    nickName = userInfo.getString("nickname");
                    headImageUrl = userInfo.getString("headimgurl");
                }
            }

            if (weChatMember == null) {
                weChatMember = new WechatMember();
                weChatMember.setOpenId(openid);
                weChatMember.setWeconfigId(configid);
                weChatMember.setNickName(nickName);// 保存微信昵称
                weChatMember.setHeadImg(headImageUrl); // 头像
                if (inviterId != null && !inviterId.equals("0") && !inviterId.equals(weChatMember.getId() + "")) {
                    weChatMember.setInviterId(Integer.parseInt(inviterId));  // 设置推荐人
                }
                weChatMember.setSynDate(new Date());
                wechatMemberMapper.insert(weChatMember);
                if (StringUtils.isBlank(weChatMember.getNickName())) {
                    weChatMember.setVipId(weChatMember.getId());
                    weChatMember.setNickName(StringUtil.convert24scaleWrapper(weChatMember.getId()).toString());
                    wechatMemberMapper.updateByPrimaryKey(weChatMember);
                }

                // 生成vip记录
                VipMember member = new VipMember();
                member.setId(weChatMember.getId());
                member.setAmount(BigDecimal.ZERO);
                member.setInviterId(weChatMember.getInviterId());
                vipMemberMapper.insert(member);
            } else if (StringUtils.isNotBlank(nickName) || StringUtils.isNotBlank(headImageUrl)) {
                VipMember vipMember = vipMemberMapper.selectByPrimaryKey(weChatMember.getId());
                if (1 == vipMember.getStatus()) {
                    throw new BizException("您已被禁止登录");
                }

                logger.info("更新用户微信信息");
                if (StringUtils.isNotBlank(nickName)) {
                    weChatMember.setNickName(nickName);// 保存微信昵称
                }
                if (StringUtils.isNotBlank(headImageUrl)) {
                    weChatMember.setHeadImg(headImageUrl);// 保存微信昵称
                }
                weChatMember.setSynDate(new Date());
                wechatMemberMapper.updateByPrimaryKey(weChatMember);
            }

            SystemContext.setCurrentUser(weChatMember.getId());
            CookieUtil.setCookie("dwjCookie", weChatMember.getId() + "_dwj", true);

            model.addAttribute("userId", weChatMember.getId());
            model.addAttribute("headImg", weChatMember.getHeadImg());

//            return "page/pages/home/home";
            return "page/pages/baijiale/baijiale";
        } else {
            return "page/pages/err";
        }
    }


    /**
     * 绑定手机号码，升级为vip 会员
     *
     * @param vipMemberVo
     * @return
     */
    @RequestMapping(value = "/bindMobile")
    @ResponseBody
    @ApiOperation("绑定手机号")
    public JSONObject bindMobile(@RequestBody VipMemberVo vipMemberVo) {
        JSONObject jsonObject = new JSONObject();

        Integer userId = vipMemberVo.getUserId();
        String mobile = vipMemberVo.getMobile();
        String verifyCode = vipMemberVo.getVerifyCode();
        logger.info("开始为用户：userId={},绑定手机号码：mobile={},短信校验码：verifyCode={}", userId, mobile, verifyCode);


        Object value = redisUtil.get(RedisKeysEnum.WX_MOBILE_VERIFY_CODE.getKey() + ":" + mobile);
        if (Objects.isNull(value)) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "验证码已过期!");
        } else {
            String srcVerifyCode = (String) value;
            if (!srcVerifyCode.equals(verifyCode)) {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "验证码输入有误!");
            } else {

                int code = weChatService.bindMobile(vipMemberVo);
                WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
                if (code == 1 && null == wechatMember.getVipId()) {
                    VipMember member = vipMemberMapper.getVipMemberByMobile(mobile);
                    code = weChatService.updateWechatMember(userId, member.getId());
                }
                jsonObject.put("code", code);
                jsonObject.put("msg", code == 1 ? "绑定成功!" : "绑定失败!");
            }
        }
        return jsonObject;
    }


    /**
     * 获取校验码
     *
     * @param vipMemberVo
     * @return
     */
    @RequestMapping("/getVerifyCode")
    @ResponseBody
    @ApiOperation("获取校验码")
    public JSONObject getVerifyCode(@RequestBody VipMemberVo vipMemberVo) {
        JSONObject jsonObject = new JSONObject();
        String mobile = vipMemberVo.getMobile();
        logger.info("开始获取短信用户：usrId={},mobile={}短信验证码...", vipMemberVo.getUserId(), mobile);

        String verifyCode = NumberUtils.create6Num();
        redisUtil.set(RedisKeysEnum.WX_MOBILE_VERIFY_CODE.getKey() + ":" + mobile, verifyCode, RedisKeysEnum.WX_MOBILE_VERIFY_CODE.getExpireIn());

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

        logger.info("完成获取短信用户：usrId={},mobile={}短信验证码：verifyCode={}...", vipMemberVo.getUserId(), mobile, verifyCode);

        jsonObject.put("code", 1);
        jsonObject.put("verifyCode", verifyCode);
        return jsonObject;
    }

    /**
     * 用户中心页面
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping("/ucenter/{userId}")
    @ApiOperation("用户中心页面")
    public String ucenter(@PathVariable(value = "userId") Integer userId, Model model) {
        WechatMember weChatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        model.addAttribute("weChatMember", weChatMember);
        if (Objects.isNull(weChatMember) || userId.intValue() != SystemContext.getCurrentUser()) {
            return "page/pages/err";
        }
        Integer vipId = weChatMember.getVipId();
        if (vipId != null) {
            VipMember vipMember = vipMemberMapper.selectByPrimaryKey(vipId);
            model.addAttribute("isVip", 1); // 是vip
            model.addAttribute("totalAmount", vipMember.getAmount());
            Map<String, Object> map = new HashMap<>();
            // 1充值，2提现，3赚入，4下注，5佣金
            map.put("vipId", vipId);
            map.put("type", 1);
            map.put("date", new Date());
            BigDecimal charge = moneyRecordMapper.selectAmountByType(map);
            map.put("type", 5);
            BigDecimal commission = moneyRecordMapper.selectAmountByType(map);
            map.put("type", 3);
            BigDecimal profit = Optional.ofNullable(moneyRecordMapper.selectAmountByType(map)).orElse(BigDecimal.ZERO);
            map.put("type", 4);
            BigDecimal invest = Optional.ofNullable(moneyRecordMapper.selectAmountByType(map)).orElse(BigDecimal.ZERO);

            model.addAttribute("commission", Objects.isNull(commission) ? BigDecimal.ZERO : commission); // 佣金
            model.addAttribute("charge", Objects.isNull(charge) ? BigDecimal.ZERO : charge); // 充值
            model.addAttribute("profitLoss", profit.subtract(invest)); // 盈亏
        } else {
            model.addAttribute("isVip", 0); // 不是vip
            model.addAttribute("totalAmount", BigDecimal.ZERO); // 总金额
            model.addAttribute("commission", BigDecimal.ZERO); // 佣金
            model.addAttribute("charge", BigDecimal.ZERO); // 充值
            model.addAttribute("profitLoss", BigDecimal.ZERO); // 盈亏
        }
        model.addAttribute("userId", userId);
        return "page/pages/ucenter/ucenter";
    }


    /**
     * 账号主页
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping("/home/{userId}")
    @ApiOperation("账号主页")
    public String home(@PathVariable(value = "userId") Integer userId, Model model) {
        Integer user = SystemContext.getCurrentUser();
        if (userId == null || userId.intValue() != user) {
            return "page/pages/err";
        }
        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("headImg", wechatMember.getHeadImg());
        return "page/pages/home/home";
    }

    /**
     * 绑定页面
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping("/bind/{userId}")
    @ApiOperation("绑定页面")
    public String bind(@PathVariable(value = "userId") Integer userId, Model model) {

        if (userId == null || "".equals(userId)) {
            return "page/pages/err";
        }
        Integer id = SystemContext.getCurrentUser();
        if (Objects.isNull(id)) {
            return "redirect:/";
        }

        if (userId.intValue() != id) {
            return "page/pages/err";
        }

        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        if (Objects.nonNull(wechatMember)) {
            Integer vipId = wechatMember.getVipId();
            if (vipId != null) {
                model.addAttribute("isVip", 1);
                VipMember vipMember = vipMemberMapper.selectByPrimaryKey(vipId);
                model.addAttribute("mobile", vipMember.getMobile());
            } else {
                model.addAttribute("isVip", 0);
                model.addAttribute("mobile", null);
            }
        } else {
            return "page/pages/err";
        }
        return "page/pages/bind/bind";
    }


//    /**
//     * 获取账单明细
//     *
//     * @param userId
//     * @param pageNum
//     * @return
//     */
//    @PostMapping(value = "/myMoneyRecord/{userId}", method = RequestMethod.GET)
//    @ResponseBody
//    public JSONObject myMoneyRecord(@PathVariable Integer userId, @Param("pageNum") int pageNum) {
//        logger.info("开始获取账号userId={},第pageNum={}页的账号明细记录...", userId, pageNum);
//        WechatMember weChatMember = wechatMemberMapper.selectByPrimaryKey(userId);
//        Map<String, Object> pageParam = new HashMap<>();
//        pageParam.put("pageFirst", pageNum);
//        pageParam.put("pageSize", 15);
//        pageParam.put("userId", weChatMember.getVipId());
//        List<MoneyRecord> moneyRecords = moneyRecordMapper.listPage(pageParam);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code", 0);
//        jsonObject.put("data", moneyRecords);
//        jsonObject.put("msg", "查询成功");
//        logger.info("获取账号userId={},第pageNum={}页的账号明细记录结束，返回结果：result={}...", userId, pageNum, jsonObject);
//        return jsonObject;
//    }


//    /**
//     * 根据传入的不同类型， 获取用户账单列表
//     *
//     * @param userId
//     * @param type   1充值，2提现，3赚入，4下注，5 佣金
//     * @return
//     */
//    @GetMapping("/getBillByType")
//    @ResponseBody
//    public BaseResponse<List<MoneyRecord>> getBillListByType(@Param("userId") Integer userId, @Param("type") Integer type, @Param("pageNum") int pageNum) {
//        BaseResponse<List<MoneyRecord>> response = BaseResponse.buildFail(BaseResponse.FAIL_CODE, "数据获取失败");
//
//        logger.info("开始获取不同类型的账单列表数据，传入参数：userId={},type={},pageNum={}", userId, type, pageNum);
//        WechatMember weChatMember = wechatMemberMapper.selectByPrimaryKey(userId);
//
//        TransTypeEnum transTypeEnum = TransTypeEnum.getObjByType(type);
//        if (Objects.isNull(transTypeEnum)) {
//            logger.error("获取不同类型的账单列表数据，传入参数：type={}有误，不在我们的指定范围内...", type);
//            response.setMessage("type 参数传入有误，不在我们的指定范围内");
//            return response;
//        }
//
//        Map<String, Object> pageParam = new HashMap<>();
//        pageParam.put("pageFirst", pageNum);
//        pageParam.put("pageSize", 15);
//        pageParam.put("userId", weChatMember.getVipId());
//        pageParam.put("type", type);
//
//        List<MoneyRecord> moneyRecords = moneyRecordMapper.listPage(pageParam);
//        response.setCode(BaseResponse.SUCCESS_CODE);
//        response.setMessage("查询成功");
//        response.setData(moneyRecords);
//        response.setTotalCount(moneyRecords.size());
//
//        logger.info("完成获取不同类型的账单列表数据，传入参数：userId={},type={},pageNum={}，返回结果：result={}",
//                userId, type, pageNum, JSONObject.toJSONString(response));
//        return response;
//
//    }


    /**
     * 微信充值接口，返回预支付交易会话标识
     *
     * @param req
     * @return
     */
    @PostMapping("/unifiedOrder")
    @ResponseBody
    @ApiOperation("微信充值接口")
    public BaseResponse<Map> getPrepayId(HttpServletRequest request, @RequestBody WxOrderReq req) {
        BaseResponse<Map> response = BaseResponse.buildFail(BaseResponse.FAIL_CODE, "获取数据失败！");
        logger.info("开始为用户：userId={}进行充值，充值金额：amount={}", req.getUserId(), req.getRechargeMoney());

        Map<String, String> data = weChatService.getPrepayId(request.getRemoteAddr(), req.getUserId(), req.getRechargeMoney());
        if (data == null || data.size() == 0) {
            return response;
        }

        logger.info("完成为用户：userId={}进行充值，充值金额：amount={}，返回结果：data={}", req.getUserId(), req.getRechargeMoney(), data);

        response.setCode(BaseResponse.SUCCESS_CODE);
        response.setMessage("获取数据成功!");
        response.setData(data);
        return response;
    }

}
