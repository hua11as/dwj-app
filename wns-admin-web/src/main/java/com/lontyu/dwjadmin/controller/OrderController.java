package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.BjlOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/6
 */
@RestController
@RequestMapping("/user/order")
public class OrderController extends AbstractController {
    @Autowired
    private BjlOrderService bjlOrderService;

    /**
     * 交易记录列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = bjlOrderService.queryPage(params);

        return R.ok().put("page", page);
    }
}
