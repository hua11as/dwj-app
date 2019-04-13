package com.lontyu.dwjwap.dto.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@Data
public class RechargeQrcodeRespVO {
    @ApiModelProperty("二维码id")
    private Integer id;

    @ApiModelProperty("二维码地址")
    private String qrCode;
}
