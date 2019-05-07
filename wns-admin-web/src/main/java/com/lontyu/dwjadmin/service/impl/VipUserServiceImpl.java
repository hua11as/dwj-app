package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.service.VipUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * vip用户
 */
@Service("vipUserService")
public class VipUserServiceImpl extends ServiceImpl<VipMemberMapper, VipMember> implements VipUserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String mobile = (String) params.get("mobile");
        Page<VipMember> page = this.selectPage(
                new Query<VipMember>(params).getPage(),
                new EntityWrapper<VipMember>()
                        .like(StringUtils.isNotBlank(mobile), "mobile", mobile)
        );
        return new PageUtils(page);
    }

    @Override
    public void update(VipMember user) {

    }
}
