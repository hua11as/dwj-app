package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.WechatOrder;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2018/12/8
 */
public interface WechatOrderService extends IService<WechatOrder> {
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 审核
     *
     * @param id          记录id
     * @param auditStatus 审核状态
     * @param id          ip地址
     */
    void withdrawAudit(Integer id, Integer auditStatus, String ip);
}
