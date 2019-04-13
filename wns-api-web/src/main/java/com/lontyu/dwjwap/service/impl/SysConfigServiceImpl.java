package com.lontyu.dwjwap.service.impl;



import com.lontyu.dwjwap.dao.SysConfigMapper;
import com.lontyu.dwjwap.entity.SysConfig;
import com.lontyu.dwjwap.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("sysConfigService")
public class SysConfigServiceImpl  implements SysConfigService {

	@Autowired
	SysConfigMapper sysConfigMapper;


	@Override
	public SysConfig getSysConfigByParamKey(String paramKey) {

		return sysConfigMapper.selectByParamKey(paramKey);
	}
}
