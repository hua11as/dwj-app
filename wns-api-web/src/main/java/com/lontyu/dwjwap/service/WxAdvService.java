package com.lontyu.dwjwap.service;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.config.FileUploadProperties;
import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.dao.SpreadInfoMapper;
import com.lontyu.dwjwap.dao.VipMemberMapper;
import com.lontyu.dwjwap.dao.WechatConfigMapper;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.entity.SpreadInfo;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.entity.WechatConfig;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.exception.BizException;
import com.lontyu.dwjwap.utils.QRCodeUtils;
import com.lontyu.dwjwap.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 微信推广service
 */
@Service
public class WxAdvService implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SpreadInfoMapper spreadInfoMapper;

    @Autowired
    WechatMemberMapper wechatMemberMapper;

    @Autowired
    WechatConfigMapper wechatConfigMapper;

    @Autowired
    WeChatService weChatService;

    @Autowired
    GlobalsConfig config;

    @Autowired
    FileUploadProperties fileUploadProperties;

    @Autowired
    VipMemberMapper vipMemberMapper;

    /**
     * 缓存微信推送报文体，让独立线程异步处理
     */
    private LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    /**
     * 生成推广url ，记录推广记录表
     *
     * @param userId
     * @return
     */
    public BaseResponse<String> createAdvRecords(Integer userId) {
        BaseResponse<String> response = new BaseResponse<>(BaseResponse.FAIL_CODE, "服务器出错!");

        logger.info("开始处理userId={]推广请求，记录推广记录...", userId);
        // 1、生成带参数的二维码
        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(wechatMember) || wechatMember.getVipId() == null) {
            logger.error("该用户：userId={} 还不是vip 会员，请先升级成为vip...", userId);
            response.setMessage("请先升级成为vip.");
            return response;
        }

        Integer configId = wechatMember.getWeconfigId();
        WechatConfig wechatConfig = wechatConfigMapper.selectByPrimaryKey(configId);
        if (Objects.isNull(wechatConfig)) {
            logger.error("该用户：userId={} ,关注渠道来源未获取到...", userId);
            response.setMessage("该用户状态异常，联系管理员!");
            return response;
        }

        String accessToken = weChatService.getWxAccessToken(wechatConfig.getAppId(),
                wechatConfig.getAppSecret(), configId);
        String ticket = Optional.ofNullable(getTicket(accessToken, userId)).orElse("");

        // 微信二维码地址
        String qrcodeUrl;
        try {
            qrcodeUrl = SystemConstant.WEXIN_SHOW_QRCODE_URL + "?ticket=" + URLEncoder.encode(ticket, "UTF-8");
        } catch (Exception e) {
            qrcodeUrl = SystemConstant.WEXIN_SHOW_QRCODE_URL + "?ticket=" + ticket;
        }

        // 本地二维码地址
        String qrcode;
        try {
            String content = config.getPromotionApi() + userId;
            String mergePath = fileUploadProperties.getUploadDir() + fileUploadProperties.getQrcodePath() + "/" + userId + ".png";
            String qrcodePath = fileUploadProperties.getUploadDir() + fileUploadProperties.getQrcodePath() + "/original-" + userId + ".png";
            String backgroundImg = fileUploadProperties.getUploadDir() + fileUploadProperties.getQrcodePath() + "/background.png";
            QRCodeUtils.addImageQRcode(content, backgroundImg, qrcodePath, mergePath, wechatMember.getNickName());
            qrcode = fileUploadProperties.getDownPath() + fileUploadProperties.getQrcodePath() + "/" + userId + ".png";
        } catch (Exception e) {
            throw new BizException("生成本地二维码异常：", e);
        }

        logger.info("用户：userId={},生成的推广url地址为：qrcodeUrl={}, qrcode={}", userId, qrcodeUrl, qrcode);

        // 2、记录推广记录表
        SpreadInfo spreadInfo = new SpreadInfo();
        spreadInfo.setOpenId(wechatMember.getOpenId());
        spreadInfo.setQrcode(qrcode);
        spreadInfo.setQrcodeUrl(qrcodeUrl);
        spreadInfo.setTicket(ticket);
        spreadInfo.setUserId(userId);
        saveSpreadInfo(spreadInfo);

        // 3、返回数据
        response.setCode(BaseResponse.SUCCESS_CODE);
        response.setMessage("获取数据成功!");
        response.setData(qrcode);
        return response;
    }


    @Transactional
    public void saveSpreadInfo(SpreadInfo spreadInfo) {
        spreadInfoMapper.insert(spreadInfo);
    }


    /**
     * 得到ticket
     *
     * @param accessToken
     * @param userId
     * @return
     */
    public String getTicket(String accessToken, Integer userId) {
        JSONObject params = new JSONObject();
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        JSONObject scene = new JSONObject();
        scene.put("scene_str", userId.toString());
        JSONObject actionInfo = new JSONObject();
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);

        logger.info("用户：userId={}，请求微信获取ticket，请求参数：params={}", userId, params.toJSONString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> httpEntity = new HttpEntity<>(params.toString(), httpHeaders);
        String url = String.format(SystemConstant.WEXIN_CREATE_PARAMS_URL, accessToken);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        String responseBody = null;
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            responseBody = responseEntity.getBody();
        }
        JSONObject json = JSONObject.parseObject(responseBody);
        return Objects.isNull(json) ? "" : json.getString("ticket");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 处理关注逻辑
        new Thread(() -> {
            while (true) {
                try {
                    String msg = blockingQueue.take();
                    logger.info("处理关注事件, [{}]", msg);
                    JSONObject json = JSONObject.parseObject(msg);
                    String openId = json.getString("FromUserName");
                    String eventKey = json.getString("EventKey");
                    String inviterId = eventKey.replace("qrscene_", "");
                    // 写入 或者更新 t_wechat_member 表
                    this.saveOrUpdateMember(openId, inviterId);
                } catch (Exception e) {
                    logger.error("写入t_wechat_member处理异常", e);
                }
            }
        }).start();
    }

    /**
     * 添加消息到阻塞队列
     *
     * @param eventBody
     */
    public void appendToQueue(String eventBody) {
        try {
            this.blockingQueue.offer(eventBody);
        } catch (Exception e) {
            logger.error("微信推送报文添加到队列失败", e);
        }
    }


    /**
     * 记录用户信息，或者更新账户信息
     *
     * @param openId
     * @param inviterId
     */
    private void saveOrUpdateMember(String openId, String inviterId) {
        WechatMember wechatMember = wechatMemberMapper.selectByOpenId(openId);
        if (Objects.nonNull(wechatMember)) {
            logger.info("微信用户：openId={}，曾经已经关注过，不做任何处理...");
            return;
        }
        Integer configId = config.getDefaultConfigId();
        WechatConfig weChatConfig = wechatConfigMapper.selectByPrimaryKey(configId);

        if (Objects.isNull(weChatConfig)) {
            logger.info("id={}，微信公众号配置参数为空!", configId);
            return;
        }
        String accessToken = weChatService.getWxAccessToken(weChatConfig.getAppId(), weChatConfig.getAppSecret(), configId);
        if (Objects.nonNull(accessToken)) {
            String wxUserInfo = weChatService.getWxUserInfo(accessToken, openId);
            if (Objects.nonNull(wxUserInfo)) {
                JSONObject userInfo = JSONObject.parseObject(wxUserInfo);
                WechatMember weChatMember = new WechatMember();
                weChatMember.setHeadImg(userInfo.getString("headimgurl"));
                weChatMember.setNickName(userInfo.getString("nickname"));
                weChatMember.setOpenId(openId);
                if (org.apache.commons.lang.math.NumberUtils.toInt(inviterId, 0) > 0) {
                    weChatMember.setInviterId(org.apache.commons.lang.math.NumberUtils.toInt(inviterId));
                }
                weChatMember.setWeconfigId(configId);
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
            }
        }
    }

}
