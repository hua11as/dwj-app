package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.SysUserEntity;
import com.lontyu.dwjadmin.entity.VipMember;

import java.util.List;
import java.util.Map;


/**
 * Vip用户
 * 
 */
public interface VipUserService extends IService<VipMember> {

	PageUtils queryPage(Map<String, Object> params);
	
	/**
	 * 修改用户
	 */
	void update(VipMember user);

}
