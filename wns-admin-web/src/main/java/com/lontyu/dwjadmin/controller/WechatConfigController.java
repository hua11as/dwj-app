package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.utils.EncryptUtil;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatConfig;
import com.lontyu.dwjadmin.service.VipUserService;
import com.lontyu.dwjadmin.service.WechatConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 微信配置管理
 * 
 */
@RestController
@RequestMapping("/wechat/config")
public class WechatConfigController extends AbstractController {
	@Autowired
	private WechatConfigService wechatConfigService;

	/**
	 * 所有配置信息列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wechatConfigService.queryPage(params);

		return R.ok().put("page", page);
	}
	

	/**
	 * 配置信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		WechatConfig wechatConfig = wechatConfigService.selectById(id);
		return R.ok().put("config", wechatConfig);
	}
	

	/**
	 * 修改用户
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WechatConfig wechatConfig){
		ValidatorUtils.validateEntity(wechatConfig, UpdateGroup.class);
		wechatConfigService.updateAllColumnById(wechatConfig);
		
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] configIds){
		return R.error("配置参数不允许删除,只能修改.");
	}
}
