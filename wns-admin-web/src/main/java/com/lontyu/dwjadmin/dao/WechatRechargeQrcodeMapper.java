package com.lontyu.dwjadmin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.WechatRechargeQrcode;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WechatRechargeQrcodeMapper extends BaseMapper<WechatRechargeQrcode> {
    void deleteByIds(@Param("ids") Long[] ids);

    void deleteAllValid();

    List<WechatRechargeQrcode> selectUsedQrcodeForUpdate();

    WechatRechargeQrcode selectByRealAmountForUpdate(@Param("realAmount") BigDecimal realAmount);

    List<WechatRechargeQrcode> selectByIdsForUpdate(@Param("ids") Long[] ids);

    List<WechatRechargeQrcode> selectValidForUpdate();
}