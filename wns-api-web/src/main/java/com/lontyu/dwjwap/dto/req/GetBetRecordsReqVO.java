package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 3:29
 */
@Data
public class GetBetRecordsReqVO extends PageReqVO {

    @ApiModelProperty(value = "查询开始时间（包含）")
    private Date startTime;

    @ApiModelProperty(value = "查询结束时间（不包含）")
    private Date endTime;

    @ApiModelProperty(hidden = true)
    private Integer userId;

    @ApiModelProperty(hidden = true)
    private Integer vipId;
}
