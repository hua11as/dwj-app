package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Cory on 2018/10/7.
 */
@ApiModel("开奖结果")
@Data
public class PrizeResultVo {

    public static final int  RESULT_NO_ORDER=0;
    public static final int  RESULT_NOT_FINISH=1;
    public static final int  RESULT_LOST=2;
    public static final int  RESULT_WIN=3;

    /**
     * 期数
     */
    @ApiModelProperty(value = "下注期数")
    private String peroid;

    @ApiModelProperty("开奖结果 0: 未下注，1 未开奖,2:输，3：赢")
    private Integer result;

    @ApiModelProperty("输/赢 结果")
    private BigDecimal  peroidAmount;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal accountAmount;
}
