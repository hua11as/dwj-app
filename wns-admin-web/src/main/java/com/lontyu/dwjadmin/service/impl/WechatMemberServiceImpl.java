package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.MoneyRecordMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.service.VipUserService;
import com.lontyu.dwjadmin.service.WechatMemberService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/5 20:32
 */
@Service("wechatMemberService")
public class WechatMemberServiceImpl extends ServiceImpl<WechatMemberMapper, WechatMember> implements WechatMemberService {

    @Autowired
    private VipUserService vipUserService;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String mobile = (String) params.get("mobile");
        String id = (String) params.get("id");
        String nickName = (String) params.get("nickName");
        String createTime = (String) params.get("createTime");
        Integer v = null;
        if (StringUtils.isNotBlank(mobile)) {
            VipMember vipMember = vipUserService.selectOne(new EntityWrapper<VipMember>()
                    .eq("mobile", mobile));
            v = null == vipMember ? -1 : vipMember.getId();
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<WechatMember>(params).getPage(),
                new EntityWrapper<WechatMember>()
                        .eq(StringUtils.isNotBlank(id), "vip_id", id)
                        .eq(null != v, "vip_id", v)
                        .like(StringUtils.isNotBlank(nickName), "nick_name", nickName)
                        .like(StringUtils.isNotBlank(createTime), "create_time", createTime)
                        .orderBy("id", false));
        fillUserMemberInfo(page.getRecords());
        return new PageUtils(page);
    }

    private void fillUserMemberInfo(List<Map<String, Object>> records) {
        records.forEach(map -> {
            Object vipId = map.get("vipId");
            if (null != vipId) {
                VipMember vm = vipUserService.selectById(Integer.parseInt(vipId.toString()));
                map.put("mobile", vm.getMobile());
                map.put("amount", vm.getAmount());
                map.put("status", vm.getStatus());
                map.put("createTime", vm.getCreateTime());
            }
            Map<String, Object> param = new HashMap<>();
            param.put("vipId", vipId);
            param.put("sign", "today");
            // 用户累计充值
            param.put("type", MoneyRecordTypeEnum.RECHARGE.getCode());
            map.put("recharge", moneyRecordMapper.selectAmountByType(param));
            // 用户累计提现
            param.put("type", MoneyRecordTypeEnum.WITHDRAW.getCode());
            map.put("withdraw", moneyRecordMapper.selectAmountByType(param));
            // 用户类型下注
            param.put("type", MoneyRecordTypeEnum.CHIP_IN.getCode());
            map.put("chipIn", moneyRecordMapper.selectAmountByType(param));
            // 用户累计赚取
            param.put("type", MoneyRecordTypeEnum.EARN.getCode());
            map.put("earn", moneyRecordMapper.selectAmountByType(param));
            // 用户累计赚取
            param.put("type", MoneyRecordTypeEnum.COMMISSION.getCode());
            map.put("commission", moneyRecordMapper.selectAmountByType(param));
        });
    }

    @Override
    public PageUtils queryUserRelationMemberPage(Integer vipId, Integer level) {
        List<Map<String, Object>> userMemberList = new ArrayList<>();
        if (null == vipId) {
            return new PageUtils(userMemberList, userMemberList.size(), userMemberList.size(), 1);
        }
        WechatMember wechatMember = this.baseMapper.selectById(vipId);
        // 上级
        userMemberList.addAll(getUpInviteUsers(wechatMember, level));
        // 下级
        userMemberList.addAll(getDownInviteUsers(wechatMember, level));
        fillUserMemberInfo(userMemberList);

        return new PageUtils(userMemberList, userMemberList.size(), userMemberList.size(), 1);
    }

    private List<Map<String, Object>> getDownInviteUsers(WechatMember wechatMember, Integer level) {
        final int MIN_LEVEL = -5;
        List<Map<String, Object>> userMemberList = new ArrayList<>();
        List<Integer> vipIds = new ArrayList<>();
        vipIds.add(wechatMember.getId());
        for (int i = -1; i >= MIN_LEVEL; i--) {
            if (CollectionUtils.isEmpty(vipIds)) {
                break;
            }

            List<Map<String, Object>> userList = this.selectMaps(new EntityWrapper<WechatMember>().in("inviter_id", vipIds));
            vipIds.clear();
            int finalI = i;
            userList.forEach(userMember -> {
                userMember.put("level", finalI);
                vipIds.add(Integer.parseInt(userMember.get("id").toString()));
            });
            if (null == level) {
                userMemberList.addAll(userList);
            } else if (level == i) {
                userMemberList.addAll(userList);
                break;
            }
        }

        return userMemberList;
    }

    private List<Map<String, Object>> getUpInviteUsers(WechatMember wechatMember, Integer level) {
        final int MAX_LEVEL = 5;
        List<Map<String, Object>> userMemberList = new ArrayList<>();
        Integer vipId = wechatMember.getInviterId();
        for (int i = 1; i <= MAX_LEVEL; i++) {
            if (null == vipId) {
                break;
            }

            Map<String, Object> userMember = this.selectMap(new EntityWrapper<WechatMember>().eq("id", vipId));
            vipId = Optional.ofNullable(userMember.get("vip_id")).map(obj -> StringUtils.isBlank(obj.toString()) ? null : obj.toString())
                    .map(Integer::parseInt).orElse(null);
            userMember.put("level", i);
            if (null == level) {
                userMemberList.add(userMember);
            } else if (level == i) {
                userMemberList.add(userMember);
                break;
            }
        }

        return userMemberList;
    }
}
