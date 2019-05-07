package com.lontyu.dwjwap.constants;

/**
 * 系统常量
 */
public class SystemConstant {

    // 微信认证url 地址
    public final static String WEIXIN_AUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    // 微信获取访问token 地址
    public final static String WEIXIN_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    // 微信获取票据 地址
    public final static String WEIXIN_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    // 微信获取用户基本信息 地址
    public final static String WEIXIN_GET_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    // 微信批量获取用户基本信息 地址
    public final static String WEIXIN_BATCH_GET_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";

    // 微信支付统一下单 地址
    public final static String WEIXIN_PAY_SUBMIT_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 微信支付统一下单 回调url 地址
    public final static String WEXIN_CALL_BACK_ORDER_URL = "http://x9b2i5.natappfree.cc/wxPay/notify"; // TODO 域名待修改

    // 微信查询订单状态 url 地址
    public final static String WEXIN_QUERY_ORDER_RESULT_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    // 微信生成带参数的二维码 获取到ticket地址
    public final static String WEXIN_CREATE_PARAMS_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";

    // 微信生成带参数的二维码 获取到url地址
    public final static String WEXIN_SHOW_QRCODE_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode";


    // 定义 状态码
    public static final String FAIL     = "FAIL";

    public static final String SUCCESS  = "SUCCESS";

}
