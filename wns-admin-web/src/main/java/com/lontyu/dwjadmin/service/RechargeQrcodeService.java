package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.WechatRechargeQrcode;
import com.lontyu.dwjadmin.vo.RechargeCallbackReqVO;

import java.util.List;
import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/21
 */
public interface RechargeQrcodeService extends IService<WechatRechargeQrcode> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteByIds(Long[] ids);

    void recoveryRechargeQrcode();

    void addRechargeQrcodeHis(WechatRechargeQrcode wechatRechargeQrcode, String callback);

    void rechargeSuccess(RechargeCallbackReqVO reqVO);

    String syncQrcode();
}
