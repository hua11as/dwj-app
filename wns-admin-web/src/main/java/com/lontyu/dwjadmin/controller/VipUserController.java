package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.utils.EncryptUtil;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.Assert;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.common.validator.group.AddGroup;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;
import com.lontyu.dwjadmin.entity.SysUserEntity;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.service.SysUserService;
import com.lontyu.dwjadmin.service.VipUserService;
import com.lontyu.dwjadmin.service.WechatMemberService;
import com.lontyu.dwjadmin.shiro.ShiroUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Vip用户
 * 
 */
@RestController
@RequestMapping("/vip/user")
public class VipUserController extends AbstractController {
	@Autowired
	private VipUserService vipUserService;

	@Autowired
	private WechatMemberService wechatMemberService;

	/**
	 * 所有用户列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
//		PageUtils page = vipUserService.queryPage(params);
		PageUtils page = wechatMemberService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 关联用户列表
	 */
	@RequestMapping("/relationList")
	public R relationList(@RequestParam Map<String, Object> params){
		Integer vipId = Optional.ofNullable(params.get("vipId")).map(obj -> StringUtils.isBlank(obj.toString()) ? null : obj.toString())
				.map(Integer::parseInt).orElse(null);
		Integer level = Optional.ofNullable(params.get("level")).map(obj -> StringUtils.isBlank(obj.toString()) ? null : obj.toString())
				.map(Integer::parseInt).orElse(null);
		PageUtils page = wechatMemberService.queryUserRelationMemberPage(vipId, level);

		return R.ok().put("page", page);
	}
	

	/**
	 * 用户信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		VipMember user = vipUserService.selectById(id);
		return R.ok().put("user", user);
	}
	

	/**
	 * 修改用户
	 */
	@RequestMapping("/update")
	public R update(@RequestBody VipMember user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);
		if(user.getPassword()!=null){ // MD5加密
			user.setPassword(EncryptUtil.encodeMD5String(user.getPassword()));
		}
		vipUserService.updateAllColumnById(user);
		
		return R.ok();
	}
	
	/**
	 * 删除用户
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] userIds){
		vipUserService.deleteBatchIds(Arrays.asList(userIds));
		return R.ok();
	}

	@RequestMapping("/updateStatus")
	public R updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
		VipMember vipMember = vipUserService.selectById(id);
		if (status.equals(vipMember.getStatus())) {
			return R.ok();
		}

		vipMember.setStatus(status);
		vipUserService.updateById(vipMember);
		return R.ok();
	}
}
