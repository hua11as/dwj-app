package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.WechatOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/8
 */
@RestController
@RequestMapping("/user/withdrawAudit")
public class WithdrawAuditController {

    @Autowired
    private WechatOrderService withdrawOrderService;

    /**
     * 提现审核列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("type", 2);
        PageUtils page = withdrawOrderService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/audit")
    public R audit(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = Integer.parseInt(params.get("id").toString());
        Integer auditStatus = Integer.parseInt(params.get("auditStatus").toString());
        withdrawOrderService.withdrawAudit(id, auditStatus, request.getLocalAddr());
        return R.ok();
    }
}
