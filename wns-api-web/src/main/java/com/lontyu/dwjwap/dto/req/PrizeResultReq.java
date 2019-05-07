package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Cory on 2018/10/7.
 */

@ApiModel("下注后开奖结果查询")
public class PrizeResultReq {

    @ApiModelProperty(value = "下注期数")
    private String peroid;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    public String getPeroid() {
        return peroid;
    }

    public void setPeroid(String peroid) {
        this.peroid = peroid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
