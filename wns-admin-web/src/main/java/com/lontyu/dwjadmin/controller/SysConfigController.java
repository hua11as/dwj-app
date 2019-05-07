package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.entity.SysConfigEntity;
import com.lontyu.dwjadmin.service.SysConfigService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 配置信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.selectById(id);

        return R.ok().put("config", config);
    }

    /**
     * 保存配置
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.save(config);

        return R.ok();
    }

    /**
     * 修改配置
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.update(config);

        return R.ok();
    }

    /**
     * 删除配置
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        Long[] notAllowDeleteIds = {1L, 2L, 3L, 4L, 5L, 6L};
        for (Long id : ids) {
            if(ArrayUtils.contains(notAllowDeleteIds,id)){
                return R.error("重要参数不能删除");
            }
        }

        sysConfigService.deleteBatch(ids);

        return R.ok();
    }

}
