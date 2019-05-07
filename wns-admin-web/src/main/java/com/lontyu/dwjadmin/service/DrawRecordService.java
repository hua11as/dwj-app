package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.BjlDrawRecords;

import java.util.Map;

/**
 * @author as
 * @desc
 * @date 2019/1/13
 */
public interface DrawRecordService extends IService<BjlDrawRecords> {
    PageUtils queryPage(Map<String, Object> params);

    void forceTie(String period);
}
