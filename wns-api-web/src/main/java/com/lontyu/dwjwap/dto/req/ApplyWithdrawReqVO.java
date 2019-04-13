package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author as
 * @desc
 * @date 2018/12/7
 */
@Data
public class ApplyWithdrawReqVO {
    @ApiModelProperty(value = "提现金额")
    private BigDecimal amount;

    @ApiModelProperty(hidden = true)
    private Integer userId;
}
