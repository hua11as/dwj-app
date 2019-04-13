package com.lontyu.dwjwap.service.impl;

import com.lontyu.dwjwap.dao.WechatRechargeQrcodeHisMapper;
import com.lontyu.dwjwap.dao.WechatRechargeQrcodeMapper;
import com.lontyu.dwjwap.dto.req.GetRechargeQrcodeReqVO;
import com.lontyu.dwjwap.dto.resp.RechargeQrcodeRespVO;
import com.lontyu.dwjwap.entity.WechatRechargeQrcode;
import com.lontyu.dwjwap.entity.WechatRechargeQrcodeHis;
import com.lontyu.dwjwap.exception.BizException;
import com.lontyu.dwjwap.service.RechargeQrcodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@Service
public class RechargeQrcodeServiceImpl implements RechargeQrcodeService {

    @Autowired
    private WechatRechargeQrcodeMapper wechatRechargeQrcodeMapper;

    @Autowired
    private WechatRechargeQrcodeHisMapper wechatRechargeQrcodeHisMapper;

    @Override
    @Transactional
    public RechargeQrcodeRespVO getRechargeQrcode(GetRechargeQrcodeReqVO rechargeQrcodeReqVO) {
        RechargeQrcodeRespVO respVO = new RechargeQrcodeRespVO();
        WechatRechargeQrcode rechargeQrcode = wechatRechargeQrcodeMapper.selectByAmountAndUserIdForUpdate(rechargeQrcodeReqVO.getAmount(),
                rechargeQrcodeReqVO.getUserId());
        if (null != rechargeQrcode) {
            respVO.setId(rechargeQrcode.getId());
            respVO.setQrCode(rechargeQrcode.getQrCode());
            return respVO;
        }

        rechargeQrcode = wechatRechargeQrcodeMapper.selectNotUsedByAmountForUpdate(rechargeQrcodeReqVO.getAmount());
        if (null == rechargeQrcode) {
            throw new BizException("当前暂无该金额可用二维码，请3分钟后再试！");
        }

        // 插入历史
        this.addRechargeQrcodeHis(rechargeQrcode);

        // 绑定用户
        Date now = new Date();
        rechargeQrcode.setBindUserId(rechargeQrcodeReqVO.getUserId());
        rechargeQrcode.setStatus(1);
        rechargeQrcode.setBindTime(now);
        rechargeQrcode.setUpdateTime(now);
        wechatRechargeQrcodeMapper.updateByPrimaryKeySelective(rechargeQrcode);

        respVO.setId(rechargeQrcode.getId());
        respVO.setQrCode(rechargeQrcode.getQrCode());
        return respVO;
    }

    private void addRechargeQrcodeHis(WechatRechargeQrcode wechatRechargeQrcode) {
        WechatRechargeQrcodeHis wechatRechargeQrcodeHis = new WechatRechargeQrcodeHis();
        BeanUtils.copyProperties(wechatRechargeQrcode, wechatRechargeQrcodeHis);
        wechatRechargeQrcodeHis.setId(null);
        wechatRechargeQrcodeHis.setOriginalId(wechatRechargeQrcode.getId());
        wechatRechargeQrcodeHisMapper.insert(wechatRechargeQrcodeHis);
    }
}
