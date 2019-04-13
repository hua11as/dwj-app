package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 *  百家乐前端页面传入参数
 */
@ApiModel
public class BjlOrderVo {

    @ApiModelProperty(value = "用户id")
    private Integer userId; // 用户id

    @ApiModelProperty(value = "产品序号 （如：V000001）")
    private String productSerial; // 产品序号 （如：V000001）

    @ApiModelProperty(value = "投注期数")
    private String periods; // 当前期数
    @ApiModelProperty(value = "押注大小 1、闲 ；2、闲对；3 、和；4、庄对；5、庄")
    private Integer selectedSize; // 押注大小
    @ApiModelProperty(value = "下注金额")
    private BigDecimal buyAmount; // 下注金额
    @ApiModelProperty(value = "0、庄家胜；1、闲胜")
    private Integer supportWin;  // 下注谁赢

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getProductSerial() {
        return productSerial;
    }

    public void setProductSerial(String productSerial) {
        this.productSerial = productSerial;
    }

    public String  getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public Integer getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(Integer selectedSize) {
        this.selectedSize = selectedSize;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public Integer getSupportWin() {
        return supportWin;
    }

    public void setSupportWin(Integer supportWin) {
        this.supportWin = supportWin;
    }
}
