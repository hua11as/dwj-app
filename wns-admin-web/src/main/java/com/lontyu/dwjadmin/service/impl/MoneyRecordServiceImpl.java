package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.constants.CommonConstants;
import com.lontyu.dwjadmin.dao.MoneyRecordMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.service.MoneyRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("moneyRecordService")
public class MoneyRecordServiceImpl extends ServiceImpl<MoneyRecordMapper, MoneyRecord> implements MoneyRecordService {

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String vipId = (String) params.get("vipId");
        String nickName = (String) params.get("nickName");
        String type = (String) params.get("type");
        String status = (String) params.get("status");
        String createTime = (String) params.get("createTime");
        Integer nickNameVipId = null;
        if (StringUtils.isNotBlank(nickName)) {
            WechatMember wmParam = new WechatMember();
            wmParam.setNickName(nickName);
            WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
            nickNameVipId = null != wechatMember ? wechatMember.getVipId() : -1;
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<MoneyRecord>(params).getPage(),
                new EntityWrapper<MoneyRecord>()
                        .eq(StringUtils.isNotBlank(vipId), "vip_id", vipId)
                        .eq(null != nickNameVipId, "vip_id", nickNameVipId)
                        .eq(StringUtils.isNotBlank(type), "type", type)
                        .eq(StringUtils.isNotBlank(status), "status", status)
                        .like(StringUtils.isNotBlank(createTime), "create_time", createTime)
                        .orderBy("create_time", false)
        );
        page.getRecords().forEach(record -> {
            int userId = Integer.parseInt(record.get("vipId").toString());
            if (CommonConstants.PLATFORM_VIP_ID == userId) {
                record.put("nickName", "平台账号");
                return;
            }
            WechatMember wechatMember = wechatMemberMapper.selectById(userId);
            record.put("nickName", wechatMember.getNickName());
        });
        return new PageUtils(page);
    }
}
