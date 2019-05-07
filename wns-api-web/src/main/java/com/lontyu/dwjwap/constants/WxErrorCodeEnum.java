package com.lontyu.dwjwap.constants;

/**
 *  微信错误码
 */
public enum WxErrorCodeEnum {

    // 统一下单错误码
    NOAUTH("NOAUTH","商户未开通此接口权限"),
    NOTENOUGH("NOTENOUGH","用户帐号余额不足"),
    ORDERPAID("ORDERPAID","商户订单已支付，无需重复操作"),
    ORDERCLOSED("ORDERCLOSED","当前订单已关闭，无法支付"),
    SYSTEMERROR("SYSTEMERROR","系统超时"),
    APPID_NOT_EXIST("APPID_NOT_EXIST","参数中缺少APPID"),
    MCHID_NOT_EXIST("MCHID_NOT_EXIST","参数中缺少MCHID"),
    APPID_MCHID_NOT_MATCH("APPID_MCHID_NOT_MATCH","appid和mch_id不匹配"),
    LACK_PARAMS("LACK_PARAMS","缺少必要的请求参数"),
    OUT_TRADE_NO_USED("OUT_TRADE_NO_USED","同一笔交易不能多次提交"),
    SIGNERROR("SIGNERROR","参数签名结果不正确"),
    XML_FORMAT_ERROR("XML_FORMAT_ERROR","XML格式错误"),
    REQUIRE_POST_METHOD("REQUIRE_POST_METHOD","未使用post传递参数"),
    POST_DATA_EMPTY("POST_DATA_EMPTY","post数据不能为空"),
    NOT_UTF8("NOT_UTF8","未使用指定编码格式"),

    // 订单查询错误码
    ORDERNOTEXIST("ORDERNOTEXIST","此交易订单号不存在");

    private String code;

    private String desc;

    private WxErrorCodeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code){
        WxErrorCodeEnum[] values = values();
        for (WxErrorCodeEnum errorEnum : values ) {
            if(errorEnum.getCode().equals(code)){
                return errorEnum.getDesc();
            }
        }
        return "未知的错误";
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
