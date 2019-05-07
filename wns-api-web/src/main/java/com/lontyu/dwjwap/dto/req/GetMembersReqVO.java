package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/8 0:52
 */
@Data
public class GetMembersReqVO {
    @ApiModelProperty(value = "会员等级 max 5")
    private Integer level;

    @ApiModelProperty(hidden = true)
    private Integer userId;

    @ApiModelProperty(hidden = true)
    private Integer vipId;
}
