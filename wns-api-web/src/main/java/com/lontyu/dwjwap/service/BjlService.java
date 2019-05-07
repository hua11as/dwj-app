package com.lontyu.dwjwap.service;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.dto.*;
import com.lontyu.dwjwap.dto.req.PrizeResultReq;
import com.lontyu.dwjwap.dto.resp.TrendChartDataRespVO;

import java.util.List;

/**
 * 百家乐相关 Service
 */
public interface BjlService {


    /**
     * 下注订单提交
     *
     * @param bjlOrderVos
     * @return
     */
     BaseResponse submitOrder(List<BjlOrderVo> bjlOrderVos)  throws Exception;


    /**
     * 检验是否切换视频地址
     *
     * @param productSerial
     * @param currentDate
     * @param currentPeriods
     * @return
     */
     JSONObject isSwitchVideo(String productSerial, String currentDate, Integer currentPeriods);


    /**
     * 查询系统当前信息
     * @return
     */
     BaseResponse<CurrentInfoVo> currentInfo() ;


    /**
     * 查询系统当前信息
     * @return
     */
    BaseResponse<PrizeResultVo> prizeResult(PrizeResultReq req) ;

    /**
     * 获取赔率配置
     * @return
     */
    Odds getOddsConfig() ;

    /**
     * 获取走势图数据
     *
     * @return 走势图数据
     */
    BaseResponse<List<TrendChartDataRespVO>> getTrendChartData();
}
