package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dao.WechatConfigDao;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatConfig;
import com.lontyu.dwjadmin.service.VipUserService;
import com.lontyu.dwjadmin.service.WechatConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 微信配置
 */
@Service("wechatConfigService")
public class WechatConfigServiceImpl extends ServiceImpl<WechatConfigDao, WechatConfig> implements WechatConfigService {


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String appId = (String)params.get("appId");

		Page<WechatConfig> page = this.selectPage(
				new Query<WechatConfig>(params).getPage(),
				new EntityWrapper<WechatConfig>()
						.like(StringUtils.isNotBlank(appId),"app_id", appId)
		);
		return new PageUtils(page);
	}

}
