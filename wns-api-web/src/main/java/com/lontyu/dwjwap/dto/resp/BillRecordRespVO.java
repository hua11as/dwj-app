package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 4:20
 */
@Data
public class BillRecordRespVO {

    @ApiModelProperty("流水id")
    private Integer id;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("流水类型（1充值+，2提现-，3赚入+，4下注-，5佣金+，9提现申请-，10提现退回+）")
    private Integer type;

    @ApiModelProperty("交易状态（0、默认值，1，成功，2，失败）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
