package com.lontyu.dwjadmin.task;

import com.lontyu.dwjadmin.constants.CommonConstants;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.BjlOrderMapper;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.entity.BjlOrder;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.service.ChargeService;
import com.lontyu.dwjadmin.service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动计算佣金 - -定时器 (一天计算一次佣金)
 */
@Component("autoJSCommisionTask")
public class AutoJSCommisionTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChargeService chargeService;

    @Autowired
    VipMemberMapper vipMemberMapper;

    @Autowired
    SysConfigService configService;

    @Autowired
    BjlOrderMapper bjlOrderMapper;

    @Transactional
    public void computeCommision() {
        logger.info("自动计算佣金,定时任务开始....");

        // 0 、 查询佣金比例
        String firstRate = configService.selectById(7).getParamValue(); // 一级代理佣金比例
        String secondRate = configService.selectById(8).getParamValue(); // 二级代理佣金比例
        String thirdRate = configService.selectById(10).getParamValue(); // 三级代理佣金比例
        String fourthRate = configService.selectById(11).getParamValue(); // 四级代理佣金比例
        String fifthRate = configService.selectById(12).getParamValue(); // 五级代理佣金比例
        String[] rates = {firstRate, secondRate, thirdRate, fourthRate, fifthRate};

        // 1、获取待计算佣金投注记录
        List<BjlOrder> dueOutOrders = bjlOrderMapper.selectDueOutCommissionOrders();
        if (dueOutOrders.size() == 0) {
            logger.info("自动计算佣金,定时任务结束,没有找到任何符合条件的记录....");
            return;
        }

        dueOutOrders.forEach(o -> {
            Integer vipId = o.getVipId(); // 获取到当前vip 用户id
            VipMember vipMember = vipMemberMapper.selectById(vipId);

            for (int i = 0; i < 5; i++) {
                Integer inviterId = vipMember.getInviterId();
                if (null == inviterId) {
                    break;
                }
                vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(inviterId);
                Integer version = vipMember.getVersion();
                payCommission(o, rates[i], inviterId, version);
            }

            o.setCountCommission(1);
            bjlOrderMapper.updateByPrimaryKey(o);
        });

        logger.info("自动计算佣金,定时任务结束....");
    }

    private void payCommission(BjlOrder bjlOrder, String rate, Integer vipId, Integer version) {
        BigDecimal commission = bjlOrder.getBuyAmount().multiply(new BigDecimal(rate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        MoneyRecord moneyRecord = new MoneyRecord();
        moneyRecord.setType(MoneyRecordTypeEnum.COMMISSION.getCode());
        moneyRecord.setStatus(1);
        moneyRecord.setAmount(commission);
        moneyRecord.setVipId(vipId);
        moneyRecord.setOrderId(bjlOrder.getId().toString());
        moneyRecord.setCreateTime(bjlOrder.getAddTime());
        moneyRecord.setRemark(MoneyRecordTypeEnum.COMMISSION.getDesc());
        chargeService.insert(moneyRecord);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", commission);
        params.put("id", vipId);
        params.put("version", version);
        vipMemberMapper.updateAoumt(params);

        VipMember sysVipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(CommonConstants.PLATFORM_VIP_ID);
        MoneyRecord sysMoneyRecord = new MoneyRecord();
        sysMoneyRecord.setType(MoneyRecordTypeEnum.PLATFORM_PAY_COMMISSION.getCode());
        sysMoneyRecord.setStatus(1);
        sysMoneyRecord.setAmount(commission);
        sysMoneyRecord.setVipId(sysVipMember.getId());
        moneyRecord.setOrderId(bjlOrder.getId().toString());
        sysMoneyRecord.setCreateTime(bjlOrder.getAddTime());
        sysMoneyRecord.setRemark(MoneyRecordTypeEnum.PLATFORM_PAY_COMMISSION.getDesc());
        chargeService.insert(sysMoneyRecord);

        params = new HashMap<>();
        params.put("amount", commission);
        params.put("id", sysVipMember.getId());
        params.put("version", sysVipMember.getVersion());
        vipMemberMapper.updateAoumt(params);
    }
}



