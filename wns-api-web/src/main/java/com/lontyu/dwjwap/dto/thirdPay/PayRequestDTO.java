package com.lontyu.dwjwap.dto.thirdPay;

import lombok.Data;
import lombok.ToString;

/**
 * @author as
 * @desc
 * @sinse 2019/4/11
 */
@Data
@ToString
public class PayRequestDTO {
    private String cusOrderNo;
    private String goodsName;
    private Integer amount;
    private Integer payType;
    private Integer payMethod;
    private String remark;
    private Long orderTime;
    private String notifyUrl;
    private String returnUrl;
    private String authCode;
    private String appId;
    private String openId;
}
