package com.lontyu.dwjwap.dto.resp;

import com.lontyu.dwjwap.dto.UserInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @decription: 用户中心信息
 * @author: as
 * @date: 2018/10/16 23:47
 */
@Data
public class UserCenterInfoRespVO {
    @ApiModelProperty("用户信息")
    private UserInfoVO userInfoVO;

    @ApiModelProperty("今日佣金")
    private BigDecimal commission;

    @ApiModelProperty("今日充值")
    private BigDecimal recharge;

    @ApiModelProperty("盈亏状况")
    private BigDecimal profitAndLoss;
}
