package com.lontyu.dwjwap.task;

import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.constants.WxErrorCodeEnum;
import com.lontyu.dwjwap.dao.WechatOrderMapper;
import com.lontyu.dwjwap.entity.WechatOrder;
import com.lontyu.dwjwap.service.WeChatService;
import com.lontyu.dwjwap.service.WxOrderService;
import com.lontyu.dwjwap.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 微信轮询查询订单状态
 */
//@Component("wxPollingOrderTask")
public class WxPollingOrderTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WechatOrderMapper wechatOrderMapper;

    @Autowired
    GlobalsConfig config;

    @Autowired
    WeChatService weChatService;

    @Autowired
    WxOrderService wxOrderService;


    /**
     * 每30分钟轮询一次没有接收到微信回调的充值记录
     */
//    @Scheduled(cron = "0 0/30 * * * ?")
    public void updateOrderStatus() {
        // 1、查询未知结果的订单
        List<WechatOrder> wechatOrders = wechatOrderMapper.selectRecordBySign(0);

        if (wechatOrders.size() == 0) {
            logger.info("当前半个小时，时间段内没有充值记录...");
            return;
        }

        // 2、遍历查询订单状态
        try {
            Map<String, String> params = new HashMap<>();
            for (WechatOrder wechatOrder : wechatOrders) {
                String orderId = wechatOrder.getOrderId();
                BigDecimal realAmount = wechatOrder.getAmount(); // 金额 ，单位： 元
                int transForAmount = realAmount.multiply(BigDecimal.valueOf(100)).intValue(); // 金额 ，单位： 分
                params.put("appid", config.getAppId());
                params.put("mch_id", config.getMchId());
                params.put("out_trade_no", orderId);
                params.put("nonce_str", WXPayUtil.generateNonceStr());
                params.put("sign", WXPayUtil.generateSignature(params, config.getPayKey()));

                // 3、组装查询请求，发往微信端
                String xmlStr = WXPayUtil.mapToXml(params);
                String result = weChatService.sendQueryToWX(SystemConstant.WEXIN_QUERY_ORDER_RESULT_URL, xmlStr);
                logger.info("请求微信查询订单结果，请求参数：xmlStr={},返回结果：result={}", xmlStr, result);

                // 4、解析返回的结果
                Map<String, String> mapStr = WXPayUtil.xmlToMap(result);
                if (SystemConstant.FAIL.equals(mapStr.get("return_code"))) {
                    logger.error("请求微信方查询订单号：orderId={},返回结果失败，失败原因： msg ={}", orderId, mapStr.get("return_msg"));
                    continue;
                }

                boolean valid = WXPayUtil.isSignatureValid(mapStr, config.getPayKey()); // 校验签名值

                if (SystemConstant.FAIL.equals(mapStr.get("result_code"))) { // 业务结果 （失败）

                    if (valid && mapStr.get("err_code").equals("ORDERNOTEXIST")) { // 订单不存在，可以更新状态为失败
                        logger.info("请求微信方查询订单号：orderId={},返回结果：msg = {}", orderId, mapStr.get("err_code_des"));
                        wxOrderService.asyncUploadOrderStatus(orderId, null, 2);

                    } else {
                        logger.error("请求微信方查询订单号：orderId={},返回结果签名值校验失败，或者微信系统异常...", orderId);
                    }

                } else {  // 业务结果 （成功）

                    if (valid && transForAmount == Integer.parseInt(mapStr.get("total_fee"))) {
                        wxOrderService.asyncUploadOrderStatus(orderId, realAmount, 1);

                    } else {
                        logger.error("请求微信方查询订单号：orderId={},返回结果签名值校验失败，或者金额对本地库不一致...", orderId);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("回调微信获取订单结果失败，失败原因：msg = {}", e.getMessage());
        }

    }

}
