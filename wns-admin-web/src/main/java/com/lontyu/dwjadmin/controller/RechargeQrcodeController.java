package com.lontyu.dwjadmin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.entity.BjlOpenprizeVideo;
import com.lontyu.dwjadmin.entity.WechatRechargeQrcode;
import com.lontyu.dwjadmin.service.RechargeQrcodeService;
import com.lontyu.dwjadmin.service.SysConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author as
 * @desc
 * @date 2018/12/21
 */
@RestController
@RequestMapping("/recharge/qrcode")
public class RechargeQrcodeController {

    @Autowired
    private RechargeQrcodeService rechargeQrcodeService;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 充值二维码列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = rechargeQrcodeService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取充值金额列表
     *
     * @return 充值金额列表
     */
    @RequestMapping("/amountList")
    public R rechargeAmountList() {
        final String rechargeAmountConfigKey = "rechargeAmountConfig";
        String rechargeAmountConfig = sysConfigService.getValue(rechargeAmountConfigKey);
        List<String> rechargeAmountConfigList = Arrays.asList(rechargeAmountConfig.split(","));
        return R.ok(Collections.singletonMap("amountList", rechargeAmountConfigList));
    }

    /**
     * 保存定时任务
     */
    @RequestMapping("/save")
    public R save(@RequestBody WechatRechargeQrcode rechargeQrcode) {
        ValidatorUtils.validateEntity(rechargeQrcode);
        List<WechatRechargeQrcode> validList = rechargeQrcodeService.selectList(new EntityWrapper<WechatRechargeQrcode>()
                .eq(null != rechargeQrcode.getRealAmount(), "real_amount", rechargeQrcode.getRealAmount())
                .eq(true, "del_flag", 0));
        if (CollectionUtils.isNotEmpty(validList)) {
            throw new RRException("实际充值金额已存在，不能重复");
        }

        rechargeQrcode.setCreateTime(new Date());
        rechargeQrcodeService.insert(rechargeQrcode);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        rechargeQrcodeService.deleteByIds(ids);
        return R.ok();
    }

    /**
     * 同步二维码
     */
    @RequestMapping("/syncQrcode")
    public R syncQrcode() {
        return R.ok(rechargeQrcodeService.syncQrcode());
    }
}
