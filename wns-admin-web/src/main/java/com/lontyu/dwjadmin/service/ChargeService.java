package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.MoneyRecord;

import java.util.List;
import java.util.Map;


/**
 * 交易记录明细
 * 
 */
public interface ChargeService extends IService<MoneyRecord> {

	PageUtils queryPage(Map<String, Object> params);


	public void updateChargeMoney(MoneyRecord moneyRecord);

	public void removeChargeMoney(MoneyRecord moneyRecord);

	public List<MoneyRecord> getEarnMoney();

	
}
