package com.lontyu.dwjadmin.vo;

import com.lontyu.dwjadmin.entity.BjlDrawRecords;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author as
 * @desc
 * @date 2019/1/13
 */
@Data
public class DrawRecodeRespVO extends BjlDrawRecords {
    private BigDecimal betAmount = BigDecimal.ZERO;
    private BigDecimal betAmount1 = BigDecimal.ZERO;
    private BigDecimal betAmount2 = BigDecimal.ZERO;
    private BigDecimal betAmount3 = BigDecimal.ZERO;
    private BigDecimal betAmount4 = BigDecimal.ZERO;
    private BigDecimal betAmount5 = BigDecimal.ZERO;
}
