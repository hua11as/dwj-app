package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@Data
public class GetRechargeQrcodeReqVO {
    @ApiModelProperty(value = "充值金额")
    private Integer amount;

    @ApiModelProperty(hidden = true)
    private Integer userId;
}
