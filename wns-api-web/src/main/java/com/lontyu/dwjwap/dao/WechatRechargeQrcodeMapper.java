package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.WechatRechargeQrcode;
import org.apache.ibatis.annotations.Param;

public interface WechatRechargeQrcodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WechatRechargeQrcode record);

    int insertSelective(WechatRechargeQrcode record);

    WechatRechargeQrcode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WechatRechargeQrcode record);

    int updateByPrimaryKey(WechatRechargeQrcode record);

    WechatRechargeQrcode selectNotUsedByAmountForUpdate(@Param("amount") Integer amount);

    WechatRechargeQrcode selectByAmountAndUserIdForUpdate(@Param("amount") Integer amount, @Param("userId") Integer userId);
}