package com.lontyu.dwjwap.utils.thirdPay;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.lontyu.dwjwap.dto.thirdPay.BaseRequestDTO;
import com.lontyu.dwjwap.dto.thirdPay.PayRequestDTO;
import com.lontyu.dwjwap.utils.HttpClientUtils;
import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.util.Date;

/**
 * @author as
 * @desc
 * @sinse 2019/4/11
 */
public class ThirdPayUtils {
//    支付接口地址：https://omi.0563hy.com
//    代付接口地址：https://ori.0563hy.com
//    商户平台地址：https://merch.0563hy.com
//    商户名：吉祥工作室
//    商户号：481961739955490932
//    交易密钥：b2d8f0819af220af1dfaf135bcf055a7

    private static final String THIRD_DOMAIN_URI = "https://omi.0563hy.com";
    private static final String PAY_INTERFACE_URI = "/pay/order";
    private static final String QUERY_INTERFACE_URI = "/pay/query";

    private static final String MERCHANT_NO = "481961739955490932";
    private static final String PAY_KEY = "b2d8f0819af220af1dfaf135bcf055a7";

    public static void main(String[] args) {
        String url = THIRD_DOMAIN_URI + PAY_INTERFACE_URI;
        Date now = new Date();
        PayRequestDTO payRequestDTO = new PayRequestDTO();
        payRequestDTO.setCusOrderNo(new Date().getTime() + "");
        payRequestDTO.setGoodsName("游戏币");
        payRequestDTO.setAmount(1);
        payRequestDTO.setPayType(2);// 0：银联；1：支付宝；2：微信；3：百度钱包；4：QQ钱包；5：京东钱包
        payRequestDTO.setPayMethod(3);//0：条码；1：扫码；2：公众号；3：WAP（H5）；4：APP；5：快捷
        payRequestDTO.setOrderTime(now.getTime());
        payRequestDTO.setReturnUrl("https://www.baidu.com");

        BaseRequestDTO requestDTO = new BaseRequestDTO(MERCHANT_NO, null,
                (int)now.getTime() / 1000, JSONObject.toJSONString(payRequestDTO));
        String json = JSONObject.toJSONString(requestDTO.generatorSign(PAY_KEY));

        String encoding = Charsets.UTF_8.displayName();
        try {
            String result = HttpClientUtils.sendPostDataByJson(url, json, encoding);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
