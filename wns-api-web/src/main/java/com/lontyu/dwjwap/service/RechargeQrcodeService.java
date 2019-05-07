package com.lontyu.dwjwap.service;

import com.lontyu.dwjwap.dto.req.GetRechargeQrcodeReqVO;
import com.lontyu.dwjwap.dto.resp.RechargeQrcodeRespVO;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
public interface RechargeQrcodeService {
    RechargeQrcodeRespVO getRechargeQrcode(GetRechargeQrcodeReqVO rechargeQrcodeReqVO);
}
