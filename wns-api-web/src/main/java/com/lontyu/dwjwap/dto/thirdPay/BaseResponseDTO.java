package com.lontyu.dwjwap.dto.thirdPay;

import lombok.Data;
import lombok.ToString;

/**
 * @author as
 * @desc
 * @sinse 2019/4/11
 */
@Data
@ToString
public class BaseResponseDTO {
    private Integer status;
    private String message;
    private String code;
    private String nonce;
    private Integer timestamp;
    private String data;
    private String sign;
}
