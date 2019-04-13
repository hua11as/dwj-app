package com.lontyu.dwjadmin.dto;

import java.math.BigDecimal;

/**
 * Created by Cory on 2018/12/21.
 */
public class CountInfoVo {

    //vip 数量
    BigDecimal vipCount=BigDecimal.ZERO;
    //充值
    BigDecimal rechargeCount=BigDecimal.ZERO;
    //提现
    BigDecimal withdrawCount=BigDecimal.ZERO;
    //平台开奖兑付
    BigDecimal outCount=BigDecimal.ZERO;
    //平台下注收入
    BigDecimal inCount=BigDecimal.ZERO;
    //邀请佣金支出
    BigDecimal invitPay=BigDecimal.ZERO;
    //日期
    String date ;
    //平台余分
    BigDecimal platformMorePoints=BigDecimal.ZERO;
    //平台输赢
    BigDecimal platformBunko=BigDecimal.ZERO;

    public BigDecimal getVipCount() {
        return vipCount;
    }

    public void setVipCount(BigDecimal vipCount) {
        this.vipCount = vipCount;
    }

    public BigDecimal getRechargeCount() {
        return rechargeCount;
    }

    public void setRechargeCount(BigDecimal rechargeCount) {
        this.rechargeCount = rechargeCount;
    }

    public BigDecimal getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(BigDecimal withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public BigDecimal getOutCount() {
        return outCount;
    }

    public void setOutCount(BigDecimal outCount) {
        this.outCount = outCount;
    }

    public BigDecimal getInCount() {
        return inCount;
    }

    public void setInCount(BigDecimal inCount) {
        this.inCount = inCount;
    }

    public BigDecimal getInvitPay() {
        return invitPay;
    }

    public void setInvitPay(BigDecimal invitPay) {
        this.invitPay = invitPay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getPlatformMorePoints() {
        return platformMorePoints;
    }

    public void setPlatformMorePoints(BigDecimal platformMorePoints) {
        this.platformMorePoints = platformMorePoints;
    }

    public BigDecimal getPlatformBunko() {
        return platformBunko;
    }

    public void setPlatformBunko(BigDecimal platformBunko) {
        this.platformBunko = platformBunko;
    }
}
