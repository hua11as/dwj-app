package com.lontyu.dwjwap.dto;

import com.lontyu.dwjwap.constants.SelectedSizeEnum;

import java.math.BigDecimal;

/**
 * Created by Cory on 2018/10/12.
 * 赔率配置
 */
public class Odds {
    BigDecimal XOdds;// 闲 1
    BigDecimal XDOdds ;// 闲对 2
    BigDecimal HOdds ;  // 和 3
    BigDecimal ZDOdds ;// 庄对 4
    BigDecimal ZOdds; // 庄 5

    public BigDecimal getXOdds() {
        return XOdds;
    }

    public void setXOdds(BigDecimal XOdds) {
        this.XOdds = XOdds;
    }

    public BigDecimal getXDOdds() {
        return XDOdds;
    }

    public void setXDOdds(BigDecimal XDOdds) {
        this.XDOdds = XDOdds;
    }

    public BigDecimal getHOdds() {
        return HOdds;
    }

    public void setHOdds(BigDecimal HOdds) {
        this.HOdds = HOdds;
    }

    public BigDecimal getZDOdds() {
        return ZDOdds;
    }

    public void setZDOdds(BigDecimal ZDOdds) {
        this.ZDOdds = ZDOdds;
    }

    public BigDecimal getZOdds() {
        return ZOdds;
    }

    public void setZOdds(BigDecimal ZOdds) {
        this.ZOdds = ZOdds;
    }

    public BigDecimal getOddsValue(SelectedSizeEnum en){
        if(en==SelectedSizeEnum.XIAN){
            return XOdds;
        }
        if(en==SelectedSizeEnum.XIANDUI){
            return XDOdds;
        }
        if(en==SelectedSizeEnum.ZHUANG){
            return ZOdds;
        }
        if(en==SelectedSizeEnum.ZHUANGDUI){
            return ZDOdds;
        }
        if(en==SelectedSizeEnum.HE){
            return HOdds;
        }
        return  null;
    }
}
