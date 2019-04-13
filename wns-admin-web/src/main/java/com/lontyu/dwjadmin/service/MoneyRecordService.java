package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.WechatConfig;

import java.util.Map;


/**
 * 交易记录明细
 * 
 */
public interface MoneyRecordService extends IService<MoneyRecord> {

	PageUtils queryPage(Map<String, Object> params);
	
}
