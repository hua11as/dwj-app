package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/8 0:58
 */
@Data
public class MemberInfoRespVO {

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("充值")
    private BigDecimal recharge;

    @ApiModelProperty("兑换")
    private BigDecimal conversion;

    @ApiModelProperty("盈亏")
    private BigDecimal profitAndLoss;

    @ApiModelProperty("金豆")
    private BigDecimal beans;

    @ApiModelProperty("下注金额")
    private BigDecimal betAmount;

    @ApiModelProperty("过去7天下注金额（date： x月x日; betAmount: xxx）")
    private List<Map<String, Object>> pastBetAmount;
}
