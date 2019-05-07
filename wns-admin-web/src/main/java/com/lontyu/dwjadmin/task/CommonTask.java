package com.lontyu.dwjadmin.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lontyu.dwjadmin.entity.WechatOrder;
import com.lontyu.dwjadmin.service.RechargeQrcodeService;
import com.lontyu.dwjadmin.service.StatisticsService;
import com.lontyu.dwjadmin.service.WechatOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@Component
public class CommonTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RechargeQrcodeService rechargeQrcodeService;

    @Autowired
    private WechatOrderService wechatOrderService;

    @Autowired
    private StatisticsService statisticsService;

    // 定时回收充值二维码
    public void recoveryRechargeQrcode() {
        try {
            rechargeQrcodeService.recoveryRechargeQrcode();
        } catch (Exception e) {
            logger.error("定时回收充值二维码异常：", e);
        }
    }

    /**
     * 提现自动审核
     */
    public void withdrawOrderAutoAudit() {
        try {
            List<WechatOrder> wechatOrderList = wechatOrderService.selectList(new EntityWrapper<WechatOrder>()
                    .eq(true, "audit_status", 0)
                    .eq(true, "type", 2)
                    .lt("amount", new BigDecimal("2000"))
                    .orderBy("create_time", true));
            final String defaultIp = "127.0.0.1";
            wechatOrderList.forEach(wechatOrder -> {
                try {
                    wechatOrderService.withdrawAudit(wechatOrder.getId(), 1, defaultIp);
                } catch (Exception e) {
                    logger.error("提现自动审核[" + wechatOrder.getId() + "]异常：", e);
                    wechatOrderService.withdrawAudit(wechatOrder.getId(), 2, defaultIp);
                }
            });
        } catch (Exception e) {
            logger.error("提现自动审核异常：", e);
        }
    }

    /**
     * 统计昨天
     */
    public void statisticsYesterday() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        statisticsService.statistics(localDate.toString(), true);
    }

    /**
     * 统计所有
     */
    public void statisticAll() {
        final String START_STAT_DATE = "2019-01-15";
        LocalDate statDate = LocalDate.parse(START_STAT_DATE);
        LocalDate now = LocalDate.now();
        while (statDate.isBefore(now)) {
            statisticsService.statistics(statDate.toString(), false);
            statDate = statDate.plusDays(1);
        }
    }
}
