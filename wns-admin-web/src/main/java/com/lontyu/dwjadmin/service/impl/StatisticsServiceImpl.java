package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.MoneyRecordMapper;
import com.lontyu.dwjadmin.dao.StatisticsMapper;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.entity.Statistics;
import com.lontyu.dwjadmin.service.StatisticsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author as
 * @desc
 * @date 2019/1/22
 */
@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements StatisticsService {

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Override
    public Statistics statistics(String statDate, Boolean ifStatMorePoints) {
        if (StringUtils.isBlank(statDate)) {
            return null;
        }

        Statistics statistics = new Statistics();
        statistics.setStatDate(statDate);
        statistics.setRegisterNum(vipMemberMapper.countNum(statDate));

        // 获取统计信息
        moneyRecordMapper.statByDate(statDate).forEach(statMap -> {
            int type = NumberUtils.toInt(statMap.get("type").toString());
            BigDecimal amount = new BigDecimal(statMap.get("amount").toString());
            MoneyRecordTypeEnum typeEnum = MoneyRecordTypeEnum.keyOf(type);
            if (null == typeEnum || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            switch (typeEnum) {
                case RECHARGE:
                    statistics.setRecharge(amount);
                    break;
                case EARN:
                    statistics.setCompensate(amount);
                    break;
                case CHIP_IN:
                    statistics.setBet(amount);
                    break;
                case COMMISSION:
                    statistics.setCommission(amount);
                    break;
                case WITHDRAW:
                    statistics.setWithdraw(statistics.getWithdraw().add(amount));
                    break;
                case WITHDRAW_APPLY:
                    statistics.setWithdraw(statistics.getWithdraw().add(amount));
                    break;
                case WITHDRAW_RETURN:
                    statistics.setWithdraw(statistics.getWithdraw().subtract(amount));
                    break;
                default:
                    break;
            }
        });

        // 统计平台余分（用户当前余额）
        if (ifStatMorePoints) {
            statistics.setMorePoints(vipMemberMapper.sumAmountWithoutPlatform());
        }

        // 平台输赢(下注金额 - 赔付金额 - 佣金)
        statistics.setBunko(statistics.getBet().subtract(statistics.getCompensate()).subtract(statistics.getCommission()));

        this.insertOrUpdate(statistics);
        return statistics;
    }

    @Override
    public List<Statistics> getStatisticsList(String startDate, String endDate) {
        return this.selectList(new EntityWrapper<Statistics>()
                .ge(StringUtils.isNotBlank(startDate), "stat_date", startDate)
                .le(StringUtils.isNotBlank(endDate), "stat_date", endDate)
                .orderBy("stat_date", false));
    }
}
