package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @decription: 走势图数据
 * @author: as
 * @date: 2018/11/20 1:44
 */
@Data
public class TrendChartDataRespVO {

    @ApiModelProperty("开奖期号（如：201808070001）")
    private String awardPeriod;

    @ApiModelProperty("中奖结果 1 闲胜 2庄胜，3 和")
    private Integer drawResult;

    @ApiModelProperty("闲家是否成对 0：否 1：是")
    private Integer playerPair;

    @ApiModelProperty("庄家是否成对 0：否 1：是")
    private Integer bankerPair;
}
