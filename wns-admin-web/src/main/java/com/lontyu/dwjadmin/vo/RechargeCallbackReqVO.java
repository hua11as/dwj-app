package com.lontyu.dwjadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@Data
public class RechargeCallbackReqVO {
    // 到账时间
    private String clock;
    // 备注信息
    private String name;
    // 订单号
    private String order;
    // 到账金额
    private BigDecimal money;
    // 卡号
    private String card;
    // 卡密
    private String dense;
    // 密钥
    private String key;
    // 类型：1=支付宝 2=财付通 3=QQ钱包 4=微信
    private Integer mode;
}
