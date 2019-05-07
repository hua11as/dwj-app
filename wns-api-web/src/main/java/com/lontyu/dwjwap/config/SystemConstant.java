package com.lontyu.dwjwap.config;

/**
 *  系统常量
 */
public class SystemConstant {

    // 微信认证url 地址
    public final static String WEIXIN_AUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    // 微信获取访问token 地址
    public final static String WEIXIN_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    // 微信获取票据 地址
    public final static String WEIXIN_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    // 微信支付统一下单 地址
    public final static String WEIXIN_PAY_SUBMIT_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //平台账号ID
    public static int PLATFORM_VIP_ID=1000;


}
