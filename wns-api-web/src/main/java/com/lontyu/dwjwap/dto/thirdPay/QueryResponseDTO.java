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
public class QueryResponseDTO {
    private String cusOrderNo;
    private String orderNo;
    private String traOrderNo;
    private Integer payType;
    private Integer payMethod;
    private Integer payStatus;
    private Long payTime;
    private Integer amount;
    private Integer payAmount;
}
