package com.lontyu.dwjwap.service;

import com.lontyu.dwjwap.dao.MoneyRecordMapper;
import com.lontyu.dwjwap.dao.VipMemberMapper;
import com.lontyu.dwjwap.dao.WechatOrderMapper;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WxOrderService {

    private static final Logger logger = LoggerFactory.getLogger(WxOrderService.class);

    @Autowired
    WechatOrderMapper wechatOrderMapper;

    @Autowired
    MoneyRecordMapper moneyRecordMapper;

    @Autowired
    VipMemberMapper vipMemberMapper;

    /**
     * 异步更新订单状态、更新流水状态、账号资金
     *
     * @param orderId
     * @param sign    1，成功，2，失败
     */
    @Transactional
    public void asyncUploadOrderStatus(String orderId, BigDecimal amount, Integer sign) {
        logger.info("异步更新订单号：orderId={},状态开始...", orderId);
        // 1、更新订单状态
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("sign", sign);
        wechatOrderMapper.updateByOrderId(params);

        Integer vipId = moneyRecordMapper.selectVipIdByOrderId(orderId);
        if (vipId == null) {
            logger.error("同步更新数据失败，通过orderId={},查询交易记录表没有记录...", orderId);
            throw new BizException("t_money_record 表中没有记录记录.");
        }
        // 2、更新流水状态
        moneyRecordMapper.updateByOrderId(params);

        if (sign == 1) {
            // 3、更新账号资金表
            VipMember vipMember = vipMemberMapper.selectByPrimaryKey(vipId);
            if (Objects.isNull(vipMember)) {
                logger.error("同步更新数据失败，通过vipId={}查询Vip 账号信息表没有记录...", vipId);
                throw new BizException("t_vip_member 表中没有记录记录.");
            }

            params.put("id", vipId);
            params.put("version", vipMember.getVersion());
            params.put("amount", amount);
            vipMemberMapper.updateAmountByVesion(params);
        }

        logger.info("异步更新订单号：orderId={},状态完成...", orderId);
    }


}
