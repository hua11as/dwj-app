package com.lontyu.dwjwap.dto.req;

import java.math.BigDecimal;

/**
 *  微信充值请求参数
 */
public class WxOrderReq {

    private Integer userId;

    private BigDecimal rechargeMoney;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(BigDecimal rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }
}
