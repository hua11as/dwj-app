package com.lontyu.dwjwap.service.impl;

import com.lontyu.dwjwap.dao.MoneyRecordMapper;
import com.lontyu.dwjwap.enums.MoneyRecordEnums.MoneyRecordTypeEnum;
import com.lontyu.dwjwap.service.MoneyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @decription: 流水接口，尽量让公共流水接口均放置此类；
 * @author: as
 * @date: 2018/10/18 23:40
 */
@Service
@Slf4j
public class MoneyRecordServiceImpl implements MoneyRecordService {

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Override
    public BigDecimal countByType(Integer vipId, MoneyRecordTypeEnum typeEnum, Date date) {
        if (null == vipId || null == typeEnum) {
            return BigDecimal.ZERO;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("vipId", vipId);
        params.put("type", typeEnum.getCode());
        params.put("date", date);
        return moneyRecordMapper.selectAmountByType(params);
    }
}
