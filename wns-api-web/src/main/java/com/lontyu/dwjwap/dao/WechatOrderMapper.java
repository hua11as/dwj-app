package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.dto.req.GetReWiRecordsReqVO;
import com.lontyu.dwjwap.entity.WechatOrder;

import java.util.List;
import java.util.Map;

public interface WechatOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WechatOrder record);

    int insertSelective(WechatOrder record);

    WechatOrder selectByPrimaryKey(Integer id);

    int updateByOrderId(Map<String, Object> params);

    int updateByPrimaryKey(WechatOrder record);

    WechatOrder selectByOrderId(String orderId);

    List<WechatOrder> selectRecordBySign(Integer sign);

    List<WechatOrder> selectPageList(GetReWiRecordsReqVO reqVO);

    int selectPageCount(GetReWiRecordsReqVO reqVO);
}