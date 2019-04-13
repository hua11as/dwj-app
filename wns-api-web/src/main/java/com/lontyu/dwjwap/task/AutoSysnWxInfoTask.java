package com.lontyu.dwjwap.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.dao.WechatConfigMapper;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dto.req.WxBatchInfoReq;
import com.lontyu.dwjwap.dto.WxUserInfoDto;
import com.lontyu.dwjwap.entity.WechatConfig;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.service.WeChatService;
import com.lontyu.dwjwap.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动同步微信账户信息 - -定时器
 */
@Component("autoSysnWxInfoTask")
public class AutoSysnWxInfoTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private WechatConfigMapper wechatConfigMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeChatService weChatService;

    /**
     * 同步微信头像、昵称到本地数据库 (每天晚上12点半进行数据同步)
     */
//    @Scheduled(cron = "0 30 0 * * ?")
    public void sysnUserInfoByWX() {
        logger.info("自动同步微信账户信息,定时任务开始....");
        while (true) {
            List<WechatMember> members = wechatMemberMapper.selectNoSysnData();
            if (members.size() == 0) {
                break;
            }

            Map<Integer, List<WechatMember>> listMap = members.stream().collect(Collectors.groupingBy(WechatMember::getWeconfigId));

            Set<Integer> weConfigId = listMap.keySet();
            for (Integer configId : weConfigId) {

                List<WechatMember> wechatMembers = listMap.get(configId);

                WechatMember member = wechatMembers.get(0);// 第一次的时候获取appid,appSecretId
                WechatConfig wechatConfig = wechatConfigMapper.selectByPrimaryKey(member.getWeconfigId());
                String accessToken = weChatService.getWxAccessToken(wechatConfig.getAppId(), wechatConfig.getAppSecret(), configId);

                if (accessToken == null) {
                    logger.error("自动通过更新微信账户信息，数据集合：wechatMembers={},数据非法!", JSONObject.toJSON(wechatMembers));
                    continue;
                }

                List<WxBatchInfoReq> reqList = new ArrayList<>();

                JSONObject jsonObj = new JSONObject();

                WxBatchInfoReq req = null;
                for (WechatMember m : wechatMembers) {
                    req = new WxBatchInfoReq();
                    req.setLang("zh_CN");
                    req.setOpenid(m.getOpenId());
                    reqList.add(req);
                }

                jsonObj.put("user_list", reqList);

                String batchUserInfos = getWeixinBatchGetUserInfos(accessToken, jsonObj.toString());
                logger.info("批量获取微信用户信息，请求参数：paramsMap={}，返回结果：result={}", jsonObj, batchUserInfos);

                if (batchUserInfos == null || batchUserInfos.indexOf("errcode") != -1) {
                    logger.info("批量获取微信用户信息，请求参数：paramsMap={},处理失败...", jsonObj);
                    continue;
                }

                // 获取微信返回数据成功，更新表数据
                JSONObject resultData = JSONObject.parseObject(batchUserInfos);
                JSONArray userInfoList = resultData.getJSONArray("user_info_list");

                List<WxUserInfoDto> infoDtos = userInfoList.toJavaList(WxUserInfoDto.class);
                for (WxUserInfoDto info : infoDtos) {
                    WechatMember wechatMember = new WechatMember();
                    wechatMember.setOpenId(info.getOpenid());
                    wechatMember.setHeadImg(info.getHeadimgurl());
                    wechatMember.setNickName(info.getNickname());
                    wechatMember.setSynDate(new Date());
                    wechatMemberMapper.sysnUpdateInfo(wechatMember);
                }
            }
        }
        logger.info("自动同步微信账户信息,定时任务结束....");
    }

    /**
     * 批量获取用户信息
     *
     * @param accessToke
     * @return
     */
    public String getWeixinBatchGetUserInfos(String accessToke, String requestJson) {
        String url = String.format(SystemConstant.WEIXIN_BATCH_GET_USER_INFO_URL, accessToke);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<String>(requestJson, httpHeaders);
            return restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            logger.error("批量获取微信用户信息失败，请求参数：paramsMap={}，错误原因：message={}", requestJson, e.getMessage());
        }
        return null;
    }

}
