package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.MoneyRecordMapper;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.service.ChargeService;
import com.lontyu.dwjadmin.wechat.WechatPayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("chargeService")
public class ChargeServiceImpl extends ServiceImpl<MoneyRecordMapper, MoneyRecord> implements ChargeService {

    @Autowired
    VipMemberMapper vipMemberMapper;

    @Autowired
    WechatMemberMapper wechatMemberMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String vipId = (String) params.get("vipId");
        String nickName = (String) params.get("nickName");
        String type = (String) params.get("type");
        Integer nickNameVipId = null;
        if (StringUtils.isNotBlank(nickName)) {
            WechatMember wmParam = new WechatMember();
            wmParam.setNickName(nickName);
            WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
            nickNameVipId = null != wechatMember ? wechatMember.getVipId() : -1;
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<Map<String, Object>>(params).getPage(),
                new EntityWrapper<MoneyRecord>()
                        .eq(StringUtils.isNotBlank(vipId), "vip_id", vipId)
                        .eq(null != nickNameVipId, "vip_id", nickNameVipId)
                        .eq(StringUtils.isNotBlank(type), "type", type)
//                        .like("remark", "0001")
                        .orderBy("create_time", false)
        );
        page.getRecords().forEach(record -> {
            Integer userId = Integer.parseInt(record.get("vipId").toString());
            WechatMember wechatMember = wechatMemberMapper.selectById(userId);
            record.put("nickName", wechatMember.getNickName());
        });

        return new PageUtils(page);
    }


    @Override
    public void updateChargeMoney(MoneyRecord moneyRecord) {
        moneyRecord.setCreateTime(new Date());
        moneyRecord.setStatus(1);
        boolean sign = super.insert(moneyRecord);
        if (sign) {
            VipMember member = vipMemberMapper.selectByPrimaryKey(moneyRecord.getVipId());
            Integer version = member.getVersion();
            Integer type = moneyRecord.getType();
            BigDecimal amount = moneyRecord.getAmount();
            if (type == 2) {
                amount = moneyRecord.getAmount().multiply(BigDecimal.valueOf(-1));
            }
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("id", member.getId());
            params.put("version", version);
            vipMemberMapper.updateAoumt(params);
        }
    }

    @Override
    public void removeChargeMoney(MoneyRecord moneyRecord) {

    }

    @Override
    public List<MoneyRecord> getEarnMoney() {
        LocalDate yesterDay = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String format = formatter.format(yesterDay);
        return this.selectList(new EntityWrapper<MoneyRecord>().eq("type", 4).like("create_time", format));
    }
}
