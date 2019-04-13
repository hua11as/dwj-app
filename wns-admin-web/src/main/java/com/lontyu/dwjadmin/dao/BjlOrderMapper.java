package com.lontyu.dwjadmin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.BjlOrder;
import com.lontyu.dwjadmin.vo.DrawRecodeRespVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BjlOrderMapper extends BaseMapper<BjlOrder> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BjlOrder record);

    BjlOrder selectByPrimaryKey(Integer id);

    BjlOrder selectByPrimaryKeyForUpdate(Integer id);

    int updateByPrimaryKey(BjlOrder record);

    List<BjlOrder> getSupportList(@Param("periods") String periods, @Param("supportWin") int supportWin);

    List<BjlOrder> getAmountByDate(Map<String, Object> params);

    List<BjlOrder> selectRecordsByResult(Integer sign);

    int updateOrderStatus(Map<String, Integer> params);

    int selectRecordsByResult(Map<String, Integer> params);

    /**
     * 查询待发奖订单
     *
     * @return
     */
    List<BjlOrder> selectDueOpenPrizeOrders();

    DrawRecodeRespVO statPeriodBetAmount(@Param("periods") String periods);

    List<BjlOrder> selectDueOutCommissionOrders();
}