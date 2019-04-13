package com.lontyu.dwjwap.service;

import com.lontyu.dwjwap.enums.MoneyRecordEnums.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @decription: 流水接口，尽量让公共流水接口均放置此类；
 * @author: as
 * @date: 2018/10/18 23:40
 */
public interface MoneyRecordService {

    /**
     * 根据流水类型统计
     *
     * @param vipId    vip ID
     * @param typeEnum 流水类型
     * @param date     统计日期
     * @return 统计总额
     */
    BigDecimal countByType(Integer vipId, MoneyRecordTypeEnum typeEnum, Date date);
}
