package com.lontyu.dwjwap.controller;

import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.constants.SystemConstant;
import com.lontyu.dwjwap.dao.WechatOrderMapper;
import com.lontyu.dwjwap.entity.WechatOrder;
import com.lontyu.dwjwap.service.WxOrderService;
import com.lontyu.dwjwap.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 微信支付结果通知回调controller
 */
@Controller
@RequestMapping("/wxPay")
public class WxCallBackController {

    private static final Logger logger = LoggerFactory.getLogger(WxCallBackController.class);

    @Autowired
    WechatOrderMapper wechatOrderMapper;

    @Autowired
    GlobalsConfig globalsConfig;

    @Autowired
    WxOrderService wxOrderService;

    @RequestMapping("/notify")
    public void callBackHandler(HttpServletRequest request, HttpServletResponse response) {
        logger.info("接收到微信充值回调请求...");
        try {
            // 1、获取微信返回的结果报文
            ServletInputStream inputStream = request.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuffer body = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                body.append(str);
            }
            br.close(); // 关闭流
            inputStream.close();

            String xmlStr = body.toString().replaceAll("\n|\r", "");
            logger.info("接收到微信充值结果通知报文：xmlStr={}", xmlStr);

            // 2、验证签名
            Map<String, String> mapStr = WXPayUtil.xmlToMap(xmlStr);
            String resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" +
                    "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            if (SystemConstant.FAIL.equals(mapStr.get("return_code"))) {
                logger.error("微信方校验失败，返回充值结果报文xmlStr={}", xmlStr);

            } else {
                boolean valid = WXPayUtil.isSignatureValid(mapStr, globalsConfig.getPayKey());
                if (!valid) {
                    logger.error("微信返回的充值结果报文xmlStr={}有误，签名不正确...", xmlStr);
                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" +
                            "<return_msg><![CDATA[签名值有误]]></return_msg>" + "</xml> ";
                } else {
                    // 3、检验金额是否一致
                    String orderId = mapStr.get("out_trade_no");
                    int amount = Integer.parseInt(mapStr.get("total_fee"));
                    WechatOrder wechatOrder = wechatOrderMapper.selectByOrderId(orderId);
                    if (Objects.isNull(wechatOrder)) {
                        logger.error("微信返回的充值结果报文xmlStr={}有误，订单号不存在...", xmlStr);
                        resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" +
                                "<return_msg><![CDATA[订单号有误]]></return_msg>" + "</xml> ";
                    } else {
                        int realAmount = wechatOrder.getAmount().multiply(BigDecimal.valueOf(100)).intValue();
                        if (amount != realAmount) {
                            logger.error("微信返回的充值结果报文xmlStr={}有误，金额不一致...", xmlStr);
                            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" +
                                    "<return_msg><![CDATA[交易金额不一致]]></return_msg>" + "</xml> ";
                        } else {
                            // 4、进行充值业务处理 （异步处理）
                            updateOrderStaus(orderId, wechatOrder, 1);
                        }
                    }
                }
            }
            // 5、响应结果
            flushResult(response, resXml);

        } catch (Exception e) {
            logger.error("处理微信返回的结果失败...");
        }

        logger.info("微信充值回调请求处理完成...");

    }

    /**
     * 输出结果
     *
     * @param response
     * @param resXml
     * @throws IOException
     */
    private void flushResult(HttpServletResponse response, String resXml) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        out.write(resXml.getBytes());
        out.flush();
        out.close();
        outputStream.close();
    }


    /**
     * 异步任务更新订单状态
     *
     * @param orderId
     */
    @Async
    public void updateOrderStaus(String orderId, WechatOrder wechatOrder, Integer sign) {
        if (wechatOrder.getSign() == 0) { // 如果订单状态已经更新，就不处理了
            wxOrderService.asyncUploadOrderStatus(orderId, wechatOrder.getAmount(), sign);
        }
    }

}
