package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Cory on 2018/10/7.
 */

@ApiModel("系统当前信息查询")
public class CurrentInfoReq {

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
