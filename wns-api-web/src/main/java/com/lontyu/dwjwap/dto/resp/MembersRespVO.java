package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/8 1:50
 */
@Data
public class MembersRespVO {
    @ApiModelProperty("成员数量")
    private Integer count;
    @ApiModelProperty("总佣金")
    private BigDecimal commission;
    @ApiModelProperty("成员")
    private List<MemberInfoRespVO> memberInfoList;
    @ApiModelProperty("返点比")
    private BigDecimal rate;
}
