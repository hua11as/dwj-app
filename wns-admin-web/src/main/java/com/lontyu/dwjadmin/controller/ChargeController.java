package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.common.validator.group.AddGroup;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import com.lontyu.dwjadmin.entity.VipMember;
import com.lontyu.dwjadmin.entity.WechatMember;
import com.lontyu.dwjadmin.service.ChargeService;
import com.lontyu.dwjadmin.service.WechatMemberService;
import com.lontyu.dwjadmin.vo.MoneyRecordSaveReqVO;
import com.lontyu.dwjadmin.wechat.WechatPayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 *  充值、提现控制器
 */
@RestController
@RequestMapping("/user/charge")
public class ChargeController {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    VipMemberMapper vipMemberMapper;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private WechatPayService wechatPayService;

    /**
     * 交易记录列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = chargeService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        MoneyRecord record = chargeService.selectById(id);
        return R.ok().put("charge", record);
    }

    @RequestMapping("/update")
    public R update(@RequestBody MoneyRecord moneyRecord){
        ValidatorUtils.validateEntity(moneyRecord, UpdateGroup.class);
        chargeService.updateAllColumnById(moneyRecord);
        return R.ok();
    }

    @RequestMapping("/save")
    public R save(@RequestBody MoneyRecordSaveReqVO moneyRecord, HttpServletRequest request){
        WechatMember wmParam = new WechatMember();
        wmParam.setVipId(moneyRecord.getVipId());
        WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
        if (null == wechatMember) {
            return R.error("vipId："+moneyRecord.getVipId()+" 用户不存在，不能进行该操作");
        }
        moneyRecord.setVipId(wechatMember.getVipId());

        VipMember member = vipMemberMapper.selectByPrimaryKey(moneyRecord.getVipId());
        if(member==null || (StringUtils.isBlank(member.getMobile()) && 2 == (moneyRecord.getType()))){
            return R.error("会员："+moneyRecord.getVipId()+" 未绑定手机号，不能进行该操作");
        }
        ValidatorUtils.validateEntity(moneyRecord, AddGroup.class);
        moneyRecord.setRemark("0001");
        //充值不需要微信
        if(moneyRecord.getType()==1){
            chargeService.updateChargeMoney(moneyRecord);
            return R.ok();
        }

        // 冻结用户不允许提现
        if (member.getStatus() == 1) {
            return R.error("会员："+moneyRecord.getVipId()+" 已被冻结，不能进行该操作");
        }

        // 提现调用微信提现接口
        int amount = moneyRecord.getAmount().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN).intValue();
        WechatPayService.PayResponseVO payResponseVO = null;
//        try {
//            payResponseVO = wechatPayService.pay(wechatMember.getOpenId(), amount,
//                    URLEncoder.encode("微信提现操作", "UTF-8"), request.getLocalAddr());
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        payResponseVO = wechatPayService.pay(wechatMember.getOpenId(), amount, "微信提现操作", request.getLocalAddr());
        if (null != payResponseVO && payResponseVO.isPaySuccess()) {
            chargeService.updateChargeMoney(moneyRecord);
            return R.ok();
        } else {
            return R.error("微信支付失败：" + Optional.ofNullable(payResponseVO).map(Object::toString).orElse("null"));
        }
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){

        chargeService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
}
