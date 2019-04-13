package com.lontyu.dwjadmin.task;

import com.lontyu.dwjadmin.constants.CommonConstants;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.constants.PrizeResultEnum;
import com.lontyu.dwjadmin.constants.SysParamEnum;
import com.lontyu.dwjadmin.dao.*;
import com.lontyu.dwjadmin.entity.*;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.lontyu.dwjadmin.constants.SysParamEnum.*;

/**
 * 自动计算开奖结果 - -定时器
 */
@Component("prizeResultTask")
public class PrizeResultTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private BjlDrawRecordsMapper drawRecordsMapper;

    @Autowired
    private BjlOpenprizeVideoMapper bjlOpenprizeVideoMapper;

    /**
     * 判断当前时间是否过了截止下注时间，过了，则进行计算胜负，更新账号订单状态，更新账号信息，下发赔款。
     * //TODO 需要考虑并发账号锁的问题，分开为单个提交事务
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderInfo() throws Exception {
        logger.info("计算开奖结果,定时任务开始....");

        // 1、查询已经结束没有发奖的订单
        List<BjlOrder> result = bjlOrderMapper.selectDueOpenPrizeOrders();

        if (result.size() == 0) {
            logger.info("没有需要发奖的订单...");
            return;
        }

        Odds odds = this.getOddsConfig();

        VipMember platForm = vipMemberMapper.selectByPrimaryKeyForUpdate(CommonConstants.PLATFORM_VIP_ID);
        if (platForm == null) {
            logger.error("平台账号未配置，请先配置平台账号");
            return;
        }
        try {
            for (BjlOrder order : result) {
                BjlDrawRecords records = drawRecordsMapper.selectByAwardPeriod(order.getPeriods());
                if (records == null) {
                    logger.error("未找到对应的开奖视频信息：orderId:" + order.getId() + " vipId:" + order.getVipId());
                    continue;
                }

                BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(records.getAwardVideo());
                BjlOrder updateOrder = bjlOrderMapper.selectByPrimaryKeyForUpdate(order.getId());

                //1赢2输
                updateOrder.setFinalResult(2);

                //下注结果正确
                if (userPrizeResult(order.getSelectedSize(), video)) {
                    // 赚钱多少钱
                    BigDecimal winMoney = order.getBuyAmount().multiply(odds.getOddsValue(
                            getSelectedSizeEnum(order.getSelectedSize())).add(BigDecimal.ONE));
                    MoneyRecord moneyRecord = new MoneyRecord();
                    moneyRecord.setVipId(order.getVipId());
                    moneyRecord.setAmount(winMoney);
                    moneyRecord.setRemark(MoneyRecordTypeEnum.EARN.getDesc());
                    moneyRecord.setType(MoneyRecordTypeEnum.EARN.getCode());
                    moneyRecord.setStatus(1);
                    moneyRecord.setCreateTime(new Date());
                    moneyRecord.setOrderId(order.getId() + "");
                    moneyRecordMapper.insert(moneyRecord);

                    VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(order.getVipId());
                    vipMember.setAmount(vipMember.getAmount().add(winMoney));
                    vipMemberMapper.updateByPrimaryKey(vipMember);

                    MoneyRecord platFormMoneyRecord = new MoneyRecord();
                    platFormMoneyRecord.setVipId(CommonConstants.PLATFORM_VIP_ID);
                    platFormMoneyRecord.setAmount(winMoney);
                    platFormMoneyRecord.setRemark(MoneyRecordTypeEnum.PLATFORM_PAY_OUT.getDesc());
                    platFormMoneyRecord.setType(MoneyRecordTypeEnum.PLATFORM_PAY_OUT.getCode());
                    platFormMoneyRecord.setStatus(1);
                    platFormMoneyRecord.setCreateTime(new Date());
                    platFormMoneyRecord.setOrderId(order.getId() + "");

                    moneyRecordMapper.insert(platFormMoneyRecord);
                    platForm.setAmount(platForm.getAmount().subtract(winMoney));
                    vipMemberMapper.updateByPrimaryKey(platForm);
                    updateOrder.setFinalResult(1);
                }

                updateOrder.setRemark("已兑奖");
                bjlOrderMapper.updateByPrimaryKey(updateOrder);
            }
        } catch (Exception e) {
            logger.error("发奖异常：" + e.getMessage(), e);
            throw e;
        }
        logger.info("计算开奖结果,定时任务结束....");
    }

    /**
     * 判断结果是否中奖
     *
     * @param selectedSize 用户下注结果
     * @param video        开奖视频
     * @return
     */
    private boolean userPrizeResult(int selectedSize, BjlOpenprizeVideo video) {
        SysParamEnum sysParamEnum = getSelectedSizeEnum(selectedSize);
        if (null == sysParamEnum) {
            return false;
        }
        boolean rs = false;
        switch (sysParamEnum){
            case XIAN:
                if (video.getResultSign() == PrizeResultEnum.XWin.getCode()) {
                    rs = true;
                }
                break;
            case XIANDUI:
                if (1 == video.getPlayerPair()) {
                    rs = true;
                }
                break;
            case HE:
                if (video.getResultSign() == PrizeResultEnum.HE.getCode()) {
                    rs = true;
                }
                break;
            case ZHUANGDUI:
                if (1 == video.getBankerPair()) {
                    rs = true;
                }
                break;
            case ZHUANG:
                if (video.getResultSign() == PrizeResultEnum.ZWin.getCode()) {
                    rs = true;
                }
                break;
            default:
                break;
        }
        return rs;
    }

    private Odds getOddsConfig() throws Exception {

//         Object savedOdds = redisUtil.get(RedisKeysEnum.SYSTEM_ODDS_CONFIG.getKey());
//        if(savedOdds != null){
//            log.info("从缓存中读取配置： odds:"+JSONObject.toJSONString(savedOdds));
//           return (Odds)savedOdds;
//        }

        // 获取系统赔率
        List<SysConfigEntity> sysConfigs = sysConfigDao.selectOdds();
        if (Objects.isNull(sysConfigs) || sysConfigs.size() < 5) {
            logger.error("系统参数配置有误...");
            throw new Exception("系统参数配置有误 ");
        }

        double XOdds = Double.parseDouble(sysConfigs.get(0).getParamValue()); // 闲 1
        double XDOdds = Double.parseDouble(sysConfigs.get(1).getParamValue()); // 闲对 2
        double HOdds = Double.parseDouble(sysConfigs.get(2).getParamValue()); // 和 3
        double ZDOdds = Double.parseDouble(sysConfigs.get(3).getParamValue()); // 庄对 4
        double ZOdds = Double.parseDouble(sysConfigs.get(4).getParamValue()); // 庄 5

        Odds odds = new Odds();
        odds.setXOdds(XOdds);
        odds.setXDOdds(XDOdds);
        odds.setHOdds(HOdds);
        odds.setZDOdds(ZDOdds);
        odds.setZOdds(ZOdds);
//        redisUtil.set(RedisKeysEnum.SYSTEM_ODDS_CONFIG.getKey(), odds, RedisKeysEnum.SYSTEM_ODDS_CONFIG.getExpireIn());

        return odds;
    }

    class Odds {
        double XOdds;// 闲 1
        double XDOdds;// 闲对 2
        double HOdds;  // 和 3
        double ZDOdds;// 庄对 4
        double ZOdds; // 庄 5

        public double getXOdds() {
            return XOdds;
        }

        public void setXOdds(double XOdds) {
            this.XOdds = XOdds;
        }

        public double getXDOdds() {
            return XDOdds;
        }

        public void setXDOdds(double XDOdds) {
            this.XDOdds = XDOdds;
        }

        public double getHOdds() {
            return HOdds;
        }

        public void setHOdds(double HOdds) {
            this.HOdds = HOdds;
        }

        public double getZDOdds() {
            return ZDOdds;
        }

        public void setZDOdds(double ZDOdds) {
            this.ZDOdds = ZDOdds;
        }

        public double getZOdds() {
            return ZOdds;
        }

        public void setZOdds(double ZOdds) {
            this.ZOdds = ZOdds;
        }

        public BigDecimal getOddsValue(SysParamEnum en) {
            if (en == XIAN) {
                return new BigDecimal(XOdds);
            }
            if (en == XIANDUI) {
                return new BigDecimal(XDOdds);
            }
            if (en == ZHUANG) {
                return new BigDecimal(ZOdds);
            }
            if (en == ZHUANGDUI) {
                return new BigDecimal(ZDOdds);
            }
            if (en == HE) {
                return new BigDecimal(HOdds);
            }
            return null;
        }
    }

}
