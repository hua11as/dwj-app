package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 4:06
 */
@Data
public class ReWiRecordRespVO {

    @ApiModelProperty("支付订单号")
    private String orderid;

    @ApiModelProperty("交易金额，单位：元")
    private BigDecimal amount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("交易状态（0、默认值，1，成功，2，失败）")
    private Integer sign;

    @ApiModelProperty("交易类型（1、充值，2、提现）")
    private Integer type;
}
