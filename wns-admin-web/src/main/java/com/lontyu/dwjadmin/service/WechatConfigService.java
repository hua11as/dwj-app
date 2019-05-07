package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.WechatConfig;

import java.util.Map;


/**
 * 微信配置参数
 * 
 */
public interface WechatConfigService extends IService<WechatConfig> {

	PageUtils queryPage(Map<String, Object> params);
	
}
