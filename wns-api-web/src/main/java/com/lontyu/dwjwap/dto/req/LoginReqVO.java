package com.lontyu.dwjwap.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/22 23:29
 */
@Data
public class LoginReqVO {
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("验证码")
    private String verifyCode;
}
