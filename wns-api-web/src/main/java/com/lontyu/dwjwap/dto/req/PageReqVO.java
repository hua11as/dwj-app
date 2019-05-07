package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 3:21
 */
@Data
public class PageReqVO {
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "记录数", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(hidden = true)
    private Integer offset;

    public Integer getOffset() {
        return pageSize * (pageNum - 1);
    }
}
