package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *  百家乐前端页面传入参数
 */
@ApiModel("下注内容")
public class SubmitOrderVo {

    @ApiModelProperty(value = "用户id")
    private Integer userId; // 用户id

    @ApiModelProperty(value = "投注期数")
    private String periods; // 当前期数

    @ApiModelProperty(value = "下注金额 [100,200,50,50,0] (1、闲 ；2、闲对；3 、和；4、庄对；5、庄)")
    protected List<BigDecimal> buyAmount; // 下注金额

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public List<BigDecimal> getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(List<BigDecimal> buyAmount) {
        this.buyAmount = buyAmount;
    }
}
