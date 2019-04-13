package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.dto.req.GetBillDetailsReqVO;
import com.lontyu.dwjwap.dto.req.GetReWiRecordsReqVO;
import com.lontyu.dwjwap.entity.MoneyRecord;
import com.lontyu.dwjwap.entity.WechatOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MoneyRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MoneyRecord record);

    MoneyRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(MoneyRecord record);

    int updateByOrderId(Map<String,Object> params);

    List<MoneyRecord> listPage(Map<String, Object> params);

    int selectVipIdByOrderId(String orederId);

    BigDecimal selectAmountByType(Map<String, Object> params);

    List<MoneyRecord> selectPageList(GetBillDetailsReqVO reqVO);

    int selectPageCount(GetBillDetailsReqVO reqVO);
}