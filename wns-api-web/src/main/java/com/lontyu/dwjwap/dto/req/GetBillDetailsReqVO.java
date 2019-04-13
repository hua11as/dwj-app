package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 3:29
 */
@Data
public class GetBillDetailsReqVO extends PageReqVO {

    @ApiModelProperty(value = "查询开始时间（包含）")
    private Date startTime;

    @ApiModelProperty(value = "查询结束时间（不包含）")
    private Date endTime;

    @ApiModelProperty("交易类型（1充值，2提现，3赚入，4下注，5佣金）")
    private Integer type;

    @ApiModelProperty("交易状态（0、默认值，1，成功，2，失败）")
    private Integer status;

    @ApiModelProperty(hidden = true)
    private Integer userId;

    @ApiModelProperty(hidden = true)
    private Integer vipId;
}
