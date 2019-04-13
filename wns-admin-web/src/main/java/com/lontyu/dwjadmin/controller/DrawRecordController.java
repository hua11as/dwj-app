package com.lontyu.dwjadmin.controller;

import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.DrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2019/1/13
 */
@RestController
@RequestMapping("/drawRecord")
public class DrawRecordController {

    @Autowired
    private DrawRecordService drawRecordService;

    /**
     * 交易记录列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = drawRecordService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("forceTie")
    public R forceTie(@RequestBody String period) {
        drawRecordService.forceTie(period);
        return R.ok();
    }
}
