package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.MoneyRecordMapper;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dto.CountInfoVo;
import com.lontyu.dwjadmin.entity.Statistics;
import com.lontyu.dwjadmin.service.StatisticsService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 */
@RestController
@RequestMapping("/sys/")
public class SysInfoController extends AbstractController {
    @Autowired
    MoneyRecordMapper moneyRecordMapper;

    @Autowired
    VipMemberMapper vipMemberMapper;

    @Autowired
    StatisticsService statisticsService;

    /**
     * 首页面统计
     */
    @RequestMapping("/count/index")
    public R indexCountInfo() {
        BigDecimal vipCount = BigDecimal.ZERO;
        BigDecimal rechargeCount = BigDecimal.ZERO;
        BigDecimal withdrawCount = BigDecimal.ZERO;
        BigDecimal inCount = BigDecimal.ZERO;
        BigDecimal outCount = BigDecimal.ZERO;


        vipCount = new BigDecimal(vipMemberMapper.countNum(null));

        Map<String, Object> condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.RECHARGE.getCode());
        rechargeCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.WITHDRAW.getCode());
        withdrawCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.CHIP_IN.getCode());
        inCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.PLATFORM_PAY_OUT.getCode());
        outCount = moneyRecordMapper.selectAmountByType(condition);

        BigDecimal tVipCount = BigDecimal.ZERO;
        BigDecimal tRechargeCount = BigDecimal.ZERO;
        BigDecimal tWithdrawCount = BigDecimal.ZERO;
        BigDecimal tInCount = BigDecimal.ZERO;
        BigDecimal tOutCount = BigDecimal.ZERO;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.RECHARGE.getCode());
        condition.put("countDate", date);
        tRechargeCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.WITHDRAW.getCode());
        condition.put("countDate", date);
        tWithdrawCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.CHIP_IN.getCode());
        condition.put("countDate", date);
        tInCount = moneyRecordMapper.selectAmountByType(condition);

        condition = new HashedMap();
        condition.put("type", MoneyRecordTypeEnum.PLATFORM_PAY_OUT.getCode());
        condition.put("countDate", date);
        tOutCount = moneyRecordMapper.selectAmountByType(condition);


        Map<String, Object> result = new HashedMap();
        result.put("vipCount", vipCount);
        result.put("rechargeCount", rechargeCount);
        result.put("withdrawCount", withdrawCount);
        result.put("inCount", inCount);
        result.put("outCount", outCount);

        result.put("tVipCount", tVipCount);
        result.put("tRechargeCount", tRechargeCount);
        result.put("tWithdrawCount", tWithdrawCount);
        result.put("tInCount", tInCount);
        result.put("tOutCount", tOutCount);

        return R.ok().put("countInfo", result);
    }

    /**
     * 首页面统计
     */
    @RequestMapping("/count/list")
    public R indexCountInfoList() {
        LocalDate maxDate = LocalDate.now().minusDays(1);
        LocalDate minDate = maxDate.minusDays(6);
        List<Statistics> statisticsList = statisticsService.getStatisticsList(minDate.toString(), maxDate.toString());
//		List<Map<String,Object>> moneys = moneyRecordMapper.countByDate(minDate.toString(), maxDate.toString());
//
//		List<CountInfoVo> vos = new ArrayList<>();
//		List<String> dates = new ArrayList<>();
//		//日期
//		for(Map<String,Object> obj:moneys){
//			if(!dates.contains(obj.get("dt").toString())){
//				dates.add(obj.get("dt").toString());
//			}
//		}
//
//		List<Map<String,Object>> vips = vipMemberMapper.countList();
//
//		for(String date:dates){
//			CountInfoVo tv = new CountInfoVo();
//			tv.setDate(date);
//			for(Map<String,Object> obj:moneys){
//				if(obj.get("dt").toString().equals(date)){
//					this.fillCountInfoVo(tv, obj);
//				}
//			}
//			// 平台余分 = 充值-下注-提现+提现退回+赔付+佣金
//			tv.setPlatformMorePoints(tv.getRechargeCount().subtract(tv.getInCount()).subtract(tv.getWithdrawCount())
//					.add(tv.getOutCount()).add(tv.getInvitPay()));
//			// 平台输赢 = 下注-赔付-佣金
//			tv.setPlatformBunko(tv.getInCount().subtract(tv.getOutCount()).subtract(tv.getInvitPay()));
//			//VIP统计
//			for(Map<String,Object> vMap:vips){
//				if(vMap.get("dt").toString().equals(date)){
//					tv.setVipCount(new BigDecimal(vMap.get("ct").toString()));
//				}
//			}
//
//			vos.add(tv);
//		}

        PageUtils page = new PageUtils(statisticsList, statisticsList.size(), statisticsList.size(), 1);
        return R.ok().put("page", page);
    }

    /**
     * 首页面统计
     */
    @RequestMapping("/count/listAddUp")
    public R indexCountInfoListAddUp() {
//        List<CountInfoVo> vos = new ArrayList<>();
        LocalDate minDate = LocalDate.now();
        List<Statistics> statisticsList = Collections.singletonList(statisticsService.statistics(minDate.toString(), true));
//        List<Map<String, Object>> totalMaps = moneyRecordMapper.countByDate(minDate.toString(), null);

//        CountInfoVo tv = new CountInfoVo();
//        for (Map<String, Object> tvo : totalMaps) {
//            this.fillCountInfoVo(tv, tvo);
//        }
//        // 平台余分 = 充值-下注-提现+提现退回+赔付+佣金
//        tv.setPlatformMorePoints(tv.getRechargeCount().subtract(tv.getInCount()).subtract(tv.getWithdrawCount())
//                .add(tv.getOutCount()).add(tv.getInvitPay()));
//        // 平台输赢 = 下注-赔付-佣金
//        tv.setPlatformBunko(tv.getInCount().subtract(tv.getOutCount()).subtract(tv.getInvitPay()));
//        tv.setVipCount(new BigDecimal(vipMemberMapper.countNum(minDate.toString())));
//        tv.setDate("今日");
//        vos.add(tv);

        PageUtils page = new PageUtils(statisticsList, statisticsList.size(), statisticsList.size(), 1);
        return R.ok().put("page", page);
    }

    private void fillCountInfoVo(CountInfoVo tv, Map<String, Object> tvo) {
        //充值
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.RECHARGE.getCode()) {
            tv.setRechargeCount(tv.getRechargeCount().add(new BigDecimal(tvo.get("amount").toString())));
        }
        //提现
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.WITHDRAW.getCode()) {
            tv.setWithdrawCount(tv.getWithdrawCount().add(new BigDecimal(tvo.get("amount").toString())));
        }
        //提现申请
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.WITHDRAW_APPLY.getCode()) {
            tv.setWithdrawCount(tv.getWithdrawCount().add(new BigDecimal(tvo.get("amount").toString())));
        }
        //提现退回
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.WITHDRAW_RETURN.getCode()) {
            tv.setWithdrawCount(tv.getWithdrawCount().subtract(new BigDecimal(tvo.get("amount").toString())));
        }
        //平台开奖兑付
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.PLATFORM_PAY_OUT.getCode()) {
            tv.setOutCount(tv.getOutCount().add(new BigDecimal(tvo.get("amount").toString())));
        }
        //下注收入
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.CHIP_IN.getCode()) {
            tv.setInCount(tv.getInCount().add(new BigDecimal(tvo.get("amount").toString())));
        }
        //佣金
        if (NumberUtils.toInt(tvo.get("type").toString()) == MoneyRecordTypeEnum.PLATFORM_PAY_COMMISSION.getCode()) {
            tv.setInvitPay(tv.getInvitPay().add(new BigDecimal(tvo.get("amount").toString())));
        }
    }
}
