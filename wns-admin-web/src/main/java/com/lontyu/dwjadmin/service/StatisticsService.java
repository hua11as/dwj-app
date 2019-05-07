package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.entity.Statistics;

import java.util.List;

/**
 * @author as
 * @desc
 * @date 2019/1/22
 */
public interface StatisticsService extends IService<Statistics> {

    /**
     * 统计
     *
     * @param statDate 统计日期（yyyy-MM-dd）
     * @param ifStatMorePoints 是否统计平台余分
     * @return 统计信息
     */
    Statistics statistics(String statDate, Boolean ifStatMorePoints);

    /**
     * 获取统计列表
     * @param startDate 开始统计时间
     * @param endDate 结束统计时间
     * @return 统计列表
     */
    List<Statistics> getStatisticsList(String startDate, String endDate);
}
