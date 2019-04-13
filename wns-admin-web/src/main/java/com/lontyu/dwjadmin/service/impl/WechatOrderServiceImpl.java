package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.dao.WechatOrderMapper;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.entity.WechatOrder;
import com.lontyu.dwjadmin.service.MoneyRecordService;
import com.lontyu.dwjadmin.service.WechatOrderService;
import com.lontyu.dwjadmin.wechat.WechatPayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author as
 * @desc
 * @date 2018/12/8
 */
@Service
public class WechatOrderServiceImpl extends ServiceImpl<WechatOrderMapper, WechatOrder> implements WechatOrderService {

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private MoneyRecordService moneyRecordService;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userId = (String) params.get("userId");
        Integer auditStatus = Optional.ofNullable(params.get("auditStatus")).map(Object::toString).map(s ->
                StringUtils.isBlank(s) ? null : Integer.parseInt(s)).orElse(null);
        Integer type = Optional.ofNullable(params.get("type")).map(Object::toString).map(Integer::parseInt).orElse(null);
        String nickName = (String)params.get("nickName");
        Integer nickNameVipId = null;
        if (StringUtils.isNotBlank(nickName)) {
            WechatMember wmParam = new WechatMember();
            wmParam.setNickName(nickName);
            WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
            nickNameVipId = null != wechatMember ? wechatMember.getVipId() : -1;
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<WechatOrder>(params).getPage(),
                new EntityWrapper<WechatOrder>()
                        .eq(StringUtils.isNotBlank(userId), "user_id", userId)
                        .eq(null != nickNameVipId, "user_id", nickNameVipId)
                        .eq(null != auditStatus, "audit_status", auditStatus)
                        .eq(null != type, "type", type)
                        .orderBy("create_time", false)
        );
        page.getRecords().forEach(record -> {
            WechatMember wechatMember = wechatMemberMapper.selectById(Integer.parseInt(record.get("userId").toString()));
            record.put("nickName", wechatMember.getNickName());
        });

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void withdrawAudit(Integer id, Integer auditStatus, String ip) {
        if (null == id || null == auditStatus) {
            throw new RRException("参数不完整");
        }

        if (auditStatus < 1 || auditStatus > 2) {
            throw new RRException("审核状态不正确");
        }

        WechatOrder wechatOrder = this.selectOne(
                new EntityWrapper<WechatOrder>().eq("id", id).eq("audit_status", 0).eq("type", 2));
        if (null == wechatOrder) {
            throw new RRException("提现申请记录异常");
        }

        // 审核通过
        if (1 == auditStatus) {
            auditPass(wechatOrder, ip);
        } else {
            auditNotPass(wechatOrder);
        }
    }

    /**
     * 审核通过
     *
     * @param wechatOrder 提现申请记录
     */
    private void auditPass(WechatOrder wechatOrder, String ip) {
        VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(wechatOrder.getUserId());
        if (vipMember.getStatus() == 1) {
            throw new RRException("已冻结用户["+ wechatOrder.getUserId() +"]，不能进行提现操作");
        }

        // 发起微信提现
        WechatMember wechatMember = wechatMemberMapper.selectById(wechatOrder.getUserId());
        int amount = wechatOrder.getAmount().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN).intValue();
        WechatPayService.PayResponseVO payResponseVO = wechatPayService.pay(wechatMember.getOpenId(),
                amount, "微信提现操作", ip);
        if (null == payResponseVO || !payResponseVO.isPaySuccess()) {
            throw new RRException("微信提现失败：" + Optional.ofNullable(payResponseVO).map(Object::toString).orElse("null"));
        }

        // 修改订单记录
        wechatOrder.setAuditStatus(1);
        wechatOrder.setSign(1);
        wechatOrder.setOrderId(payResponseVO.getPartner_trade_no());
        this.updateById(wechatOrder);

        // 修改提现申请流水
        MoneyRecord moneyRecord = moneyRecordService.selectOne(
                new EntityWrapper<MoneyRecord>().eq("type", MoneyRecordTypeEnum.WITHDRAW_APPLY.getCode())
                        .eq("order_id", wechatOrder.getId()));
        moneyRecord.setType(MoneyRecordTypeEnum.WITHDRAW.getCode());
        moneyRecord.setOrderId(payResponseVO.getPartner_trade_no());
        moneyRecordService.updateById(moneyRecord);
    }

    /**
     * 审核不通过
     *
     * @param wechatOrder 记录id
     */
    private void auditNotPass(WechatOrder wechatOrder) {
        // 修改订单记录
        wechatOrder.setAuditStatus(2);
        this.updateById(wechatOrder);

        // 插入流水
        Date now = new Date();
        MoneyRecord moneyRecord = new MoneyRecord();
        moneyRecord.setVipId(wechatOrder.getUserId());
        moneyRecord.setAmount(wechatOrder.getAmount());
        moneyRecord.setRemark("用户提现退回");
        moneyRecord.setStatus(1);
        moneyRecord.setType(MoneyRecordTypeEnum.WITHDRAW_RETURN.getCode());
        moneyRecord.setCreateTime(now);
        moneyRecord.setOrderId(wechatOrder.getId() + "");
        moneyRecordService.insert(moneyRecord);

        // 修改用户余额
        VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(wechatOrder.getUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("amount", wechatOrder.getAmount());
        params.put("id", vipMember.getId());
        params.put("version", vipMember.getVersion());
        vipMemberMapper.updateAoumt(params);
    }
}
