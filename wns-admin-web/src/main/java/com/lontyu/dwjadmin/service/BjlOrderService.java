package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.BjlOrder;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/6
 */
public interface BjlOrderService extends IService<BjlOrder> {
    PageUtils queryPage(Map<String, Object> params);
}
