package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @decription:
 * @author: as
 * @date: 2018/10/21 3:34
 */
@Data
public class BetRecordRespVO {

    @ApiModelProperty("记录id")
    private Integer id;

    @ApiModelProperty("购买期数")
    private String periods;

    @ApiModelProperty("下注金额")
    private BigDecimal buyAmount;

    @ApiModelProperty(value = "押注（1闲 2庄 3和）")
    private Integer supportWin;

    @ApiModelProperty(value = "下注方式（1、闲 ；2、闲对；3 、和；4、庄对；5、庄）")
    private Integer selectedSize;

    @ApiModelProperty(value = "结果（0、初始值；1、赢；2、输）")
    private Integer finalResult;

    @ApiModelProperty("下注时间")
    private Date addTime;

    @ApiModelProperty("赢多少钱")
    private BigDecimal winMoney;

    @ApiModelProperty("中奖结果 0、未开奖 1、闲胜 2、庄胜 3、和")
    private Integer drawResult;
}
