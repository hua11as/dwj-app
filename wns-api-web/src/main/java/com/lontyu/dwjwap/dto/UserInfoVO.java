package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @decription: 用户信息
 * @author: as
 * @date: 2018/10/16 23:39
 */
@Data
public class UserInfoVO {
    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("用户id")
    private Integer id;

    @ApiModelProperty("用户头像")
    private String headImg;

    @ApiModelProperty("总金额")
    private BigDecimal amount;

    @ApiModelProperty("是否vip账户")
    private Boolean isVip;

    @ApiModelProperty("佣金")
    private BigDecimal commission;
}
