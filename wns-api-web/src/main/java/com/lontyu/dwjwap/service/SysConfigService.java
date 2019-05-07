package com.lontyu.dwjwap.service;


import com.lontyu.dwjwap.entity.SysConfig;
/**
 * 系统配置信息
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月4日 下午6:49:01
 */
public interface SysConfigService{

	 SysConfig getSysConfigByParamKey(String paramKey);
	
}
