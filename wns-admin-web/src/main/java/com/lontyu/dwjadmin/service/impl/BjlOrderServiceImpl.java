package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.BjlOrderMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.entity.BjlOrder;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.service.BjlOrderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/6
 */
@Service
public class BjlOrderServiceImpl extends ServiceImpl<BjlOrderMapper, BjlOrder> implements BjlOrderService {

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String vipId = (String)params.get("vipId");
        String nickName = (String)params.get("nickName");
        String periods = (String)params.get("periods");
        Integer nickNameVipId = null;
        if (StringUtils.isNotBlank(nickName)) {
            WechatMember wmParam = new WechatMember();
            wmParam.setNickName(nickName);
            WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
            nickNameVipId = null != wechatMember ? wechatMember.getVipId() : -1;
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<Map<String, Object>>(params).getPage(),
                new EntityWrapper<BjlOrder>()
                        .setSqlSelect("vip_id as vipId,periods,sum(buy_amount) as buyAmount," +
                                "selected_size as selectedSize,final_result as finalResult,max(add_time) as addTime")
                        .eq(StringUtils.isNotBlank(vipId),"vip_id", vipId)
                        .eq(null != nickNameVipId, "vip_id", nickNameVipId)
                        .eq(StringUtils.isNotBlank(periods),"periods", periods)
                        .orderBy("periods", false)
                        .groupBy("vip_id,periods,selected_size,final_result")
        );
        page.getRecords().forEach(record -> {
            Integer userId = Integer.parseInt(record.get("vipId").toString());
            WechatMember wechatMember = wechatMemberMapper.selectById(userId);
            record.put("nickName", wechatMember.getNickName());
        });

        return new PageUtils(page);
    }
}
