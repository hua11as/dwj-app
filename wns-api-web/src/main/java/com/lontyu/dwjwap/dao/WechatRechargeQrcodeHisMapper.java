package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.WechatRechargeQrcodeHis;

public interface WechatRechargeQrcodeHisMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WechatRechargeQrcodeHis record);

    int insertSelective(WechatRechargeQrcodeHis record);

    WechatRechargeQrcodeHis selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WechatRechargeQrcodeHis record);

    int updateByPrimaryKey(WechatRechargeQrcodeHis record);
}