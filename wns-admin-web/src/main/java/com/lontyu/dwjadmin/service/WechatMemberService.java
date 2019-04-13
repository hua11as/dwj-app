package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.WechatMember;

import java.util.Map;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/5 20:31
 */
public interface WechatMemberService extends IService<WechatMember> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUserRelationMemberPage(Integer vipId, Integer level);
}
