package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.RechargeQrcodeService;
import com.lontyu.dwjadmin.vo.RechargeCallbackReqVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author as
 * @desc
 * @date 2018/12/22
 */
@RestController
@RequestMapping("/callback")
public class CallbackController {

    @Autowired
    private RechargeQrcodeService rechargeQrcodeService;

    @RequestMapping("/rechargeCallback")
    public R rechargeCallback(RechargeCallbackReqVO reqVO) {
        if (null == reqVO || null == reqVO.getMoney() || StringUtils.isBlank(reqVO.getKey())
                || null == reqVO.getMode()) {
            return R.error("参数校验不通过");
        }

        // 目前只开通微信充值
        if (4 != reqVO.getMode()) {
            return R.error("参数校验不通过");
        }

        final String key = "1234567890";
        // 校验密钥
        if (!key.equals(reqVO.getKey())) {
            return R.error("参数校验不通过");
        }

        rechargeQrcodeService.rechargeSuccess(reqVO);
        return R.ok();
    }
}
