package com.lontyu.dwjwap.service;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.constants.RedisKeysEnum;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.dao.MoneyRecordMapper;
import com.lontyu.dwjwap.dao.VipMemberMapper;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dao.WechatOrderMapper;
import com.lontyu.dwjwap.entity.MoneyRecord;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.entity.WechatOrder;
import com.lontyu.dwjwap.dto.VipMemberVo;
import com.lontyu.dwjwap.utils.EncryptUtil;
import com.lontyu.dwjwap.utils.RedisUtil;
import com.lontyu.dwjwap.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WeChatService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatService.class);

    @Autowired
    VipMemberMapper vipMemberMapper;

    @Autowired
    WechatMemberMapper wechatMemberMapper;

    @Autowired
    WechatOrderMapper wechatOrderMapper;

    @Autowired
    MoneyRecordMapper moneyRecordMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GlobalsConfig globalsConfig;

    @Autowired
    RedisUtil redisUtil;

    @Transactional
    public int  bindMobile(VipMemberVo vipMemberVo) {
        Integer vipInviterId = null; // 推广人id
        WechatMember wMember = wechatMemberMapper.selectByPrimaryKey(vipMemberVo.getUserId());
        if(Objects.nonNull(wMember)){
            Integer inviterId = wMember.getInviterId();
            wMember = wechatMemberMapper.selectByPrimaryKey(inviterId);
            if(Objects.nonNull(wMember)){
                vipInviterId = wMember.getVipId();
            }
        }
        logger.info("用户userId={}的介绍人在t_vip_member 表的 vipId={}",vipMemberVo.getUserId(), vipInviterId);

        VipMember member = vipMemberMapper.selectByPrimaryKey(vipMemberVo.getUserId());
        if (null == member) {
            member = new VipMember();
            member.setId(vipMemberVo.getUserId());
            member.setMobile(vipMemberVo.getMobile());
            member.setAmount(BigDecimal.ZERO);
            member.setVerifyCode(vipMemberVo.getVerifyCode());
            member.setInviterId(vipInviterId);
            return vipMemberMapper.insert(member);
        } else {
            member.setMobile(vipMemberVo.getMobile());
            member.setVerifyCode(vipMemberVo.getVerifyCode());
            member.setInviterId(vipInviterId);
            return vipMemberMapper.updateByPrimaryKey(member);
        }
    }


    @Transactional
    public int updateWechatMember(Integer userId,Integer vipId){
        WechatMember wechatMember = new WechatMember();
        wechatMember.setId(userId);
        wechatMember.setVipId(vipId);
        int code = wechatMemberMapper.updateByPrimaryKey(wechatMember);
        if(code == 0){
            vipMemberMapper.deleteByPrimaryKey(vipId);
        }
        return code;
    }

    /**
     * 返回预支付交易会话id
     *
     * @param ip
     * @param userId
     * @param amount
     * @return
     */
    @Transactional
    public Map<String, String> getPrepayId(String ip, Integer userId, BigDecimal amount) {
        Map<String, String> signMap = new HashMap<>();

        logger.info("开始获取用户userId={}的Openid...", userId);
        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        logger.info("完成获取用户userId={}的Openid...，返回结果：wechatMember={}", userId, wechatMember);
        if (Objects.isNull(wechatMember)) {
            logger.error("预支付交易会话出错，传入的userId={}有误。", userId);
            return null;
        }

        String openId = wechatMember.getOpenId(); // 微信方id
        String orderId = UUID.randomUUID().toString().replaceAll("-", ""); // 订单号

        // 1、 开始组装发往微信的统一下单的充值报文
        String xmlData = splitXmlData(ip, amount, orderId);
        if (null == xmlData) {
            return null;
        }

        // 2、发送请求至微信端
        String resData = sendQueryToWX(SystemConstant.WEIXIN_PAY_SUBMIT_ORDER_URL, xmlData);

        // 3、解析返回的数据
        try {
            Map<String, String> resultMap = processResponseXml(resData);
            String returnCode = (String) resultMap.get("return_code");//通信标识
            String resultCode = (String) resultMap.get("result_code");//交易标识

            //只有当returnCode与resultCode均返回“success”，才代表微信支付统一下单成功
            if (SystemConstant.SUCCESS.equals(resultCode) && SystemConstant.SUCCESS.equals(returnCode)) {
                String appId = (String) resultMap.get("appid");//微信公众号AppId
                long timeStamp = WXPayUtil.getCurrentTimestamp();//当前时间戳
                String prepayId = "prepay_id=" + resultMap.get("prepay_id");//统一下单返回的预支付id
                String nonceStr = WXPayUtil.generateNonceStr();//不长于32位的随机字符串

                // 组装前端需要数据
                signMap.put("appId", appId);
                signMap.put("package", prepayId);
                signMap.put("timeStamp", String.valueOf(timeStamp));
                signMap.put("nonceStr", nonceStr);
                signMap.put("signType", "MD5");
                signMap.put("paySign", WXPayUtil.generateSignature(signMap, globalsConfig.getPayKey()));
            } else {
                String errDesc = resultMap.get("return_msg");
                logger.error("用户userId={}，发起预支付交易会话出错，错误原因：msg={}", userId, errDesc);
                return signMap;
            }

        } catch (Exception e) {
            logger.error("解析数据出错，错误原因：msg={}", e.getMessage());
        }

        // 4、记录微信支付订单表
        logger.info("开始记录用户userId={}的微信支付订单表...", userId);
        WechatOrder wechatOrder = new WechatOrder();
        wechatOrder.setAmount(amount);
        wechatOrder.setOrderId(orderId);
        wechatOrder.setType(1);
        wechatOrder.setUserId(userId);
        wechatOrderMapper.insert(wechatOrder);
        logger.info("完成记录用户userId={}的微信支付订单表...", userId);

        // 5、记录交易流水
        logger.info("开始记录用户userId={}的交易流水表...", userId);
        Integer vipId = wechatMember.getVipId();
        MoneyRecord moneyRecord = new MoneyRecord();
        moneyRecord.setVipId(vipId);
        moneyRecord.setAmount(amount);
        moneyRecord.setType(1);
        moneyRecord.setStatus(0);
        moneyRecord.setOrderId(orderId);
        moneyRecord.setCreateTime(new Date());
        moneyRecordMapper.insert(moneyRecord);
        logger.info("完成记录用户userId={}的交易流水表...", userId);

        return signMap;
    }

    /**
     * 组装 xml 报文
     *
     * @param ip
     * @param amount
     * @param orderId
     * @return
     */

    private String splitXmlData(String ip, BigDecimal amount, String orderId) {
        String xml = null;
        try {

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("appid", globalsConfig.getAppId());
            paramsMap.put("mch_id", globalsConfig.getMchId());
            paramsMap.put("device_info", "WEB");
            paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramsMap.put("body", globalsConfig.getProductDesc());
            paramsMap.put("detail", globalsConfig.getProductDetail());
            paramsMap.put("out_trade_no", orderId);
            paramsMap.put("fee_type", "CNY");
            paramsMap.put("total_fee", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).intValue())); // 充值金额 ，单位为分
            paramsMap.put("spbill_create_ip", ip);
            paramsMap.put("notify_url", SystemConstant.WEXIN_CALL_BACK_ORDER_URL); // 微信回调url地址
            paramsMap.put("trade_type", "JSAPI");

            xml = WXPayUtil.generateSignedXml(paramsMap, globalsConfig.getPayKey()); // 生成带上签名xml
        } catch (Exception e) {
            logger.error("请求微信充值，报文转换xml 失败.失败原因： message={}", e.getMessage());
        }
        return xml;
    }


    /**
     * 请求微信接口，获取返回结果
     *
     * @param xmlData
     * @return
     */
    public String sendQueryToWX(String url, String xmlData) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> httpEntity = new HttpEntity<>(xmlData, header);
        return restTemplate.postForObject(url, httpEntity, String.class);
    }

    /**
     * 将微信响应回来的数据转换成Map
     *
     * @param xmlStr
     * @return
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        } else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(SystemConstant.FAIL)) {
            return respData;
        } else if (return_code.equals(SystemConstant.SUCCESS)) {
            if (WXPayUtil.isSignatureValid(respData, globalsConfig.getPayKey())) {
                return respData;
            } else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        } else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 获取微信access_token
     *
     * @param appId
     * @param appSecretId
     * @param configId
     * @return
     */
    public String getWxAccessToken(String appId, String appSecretId, Integer configId) {
        String key = RedisKeysEnum.WX_ACCESS_TOKEN.getKey() + "_" + configId; // 由于我们可能会切换 公众号，所以需要动态获取
        Object values = redisUtil.get(key);
        String accessToke = null;

        if (Objects.isNull(values)) {
            String url = String.format(SystemConstant.WEIXIN_TOKEN_URL, appId, appSecretId);
            try {
                String result = restTemplate.getForObject(url, String.class);
                logger.info("请求微信端url={},获取access_token，返回结果：result={}", url, result);

                if (null != result && !"".equals(result) && result.indexOf("access_token") != -1) {
                    JSONObject json = JSONObject.parseObject(result);
                    accessToke = json.getString("access_token");
                    redisUtil.set(key, accessToke, RedisKeysEnum.WX_ACCESS_TOKEN.getExpireIn());
                }

            } catch (Exception e) {
                logger.error("请求微信端url={},获取access_token失败，失败原因：message={}", url, e.getMessage());
            }
        } else {
            accessToke = (String) values;
        }

        return accessToke;
    }


    /**
     * 获取微信用户账号信息
     *
     * @param accessToken
     * @param openId
     * @return
     */
    public String getWxUserInfo(String accessToken, String openId) {
        String url = String.format(SystemConstant.WEIXIN_GET_USER_INFO_URL, accessToken, openId);

        try {
            String result = restTemplate.getForObject(url, String.class);
            logger.info("请求微信端url={},获取openId={}的用户信息，返回结果：result={}", url, openId, result);

            if (null == result || "".equals(result) || result.indexOf("errcode") != -1) {
                return null;
            }
            return result;
        } catch (Exception e) {
            logger.error("请求微信端url={},获取openId={}的用户信息失败，失败原因：message={}", url, openId, e.getMessage());
        }
        return null;
    }

}
