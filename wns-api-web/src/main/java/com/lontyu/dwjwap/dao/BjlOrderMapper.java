package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.dto.req.GetBetRecordsReqVO;
import com.lontyu.dwjwap.entity.BjlOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BjlOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BjlOrder record);

    BjlOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(BjlOrder record);

    public List<BjlOrder> getAmountByDate(Map<String, Object> params);

    public List<BjlOrder> selectRecordsByResult(Integer sign);

    List<BjlOrder> selectUserRecordsByResult(@Param("userId") Integer userId, @Param("sign") Integer sign);

    public int updateOrderStatus(Map<String, Integer> params);

    public List<BjlOrder> getOrderByUserIdAndPeroid(@Param("vipId") Integer vipId,@Param("peroid") String peroid);

    List<BjlOrder> getSupportList(@Param("periods") String periods, @Param("supportWin") int supportWin);

    List<BjlOrder> selectPageList(GetBetRecordsReqVO reqVO);

    int selectPageCount(GetBetRecordsReqVO reqVO);

    List<BjlOrder> selectBuyAmountByPeriod(@Param("periods") String periods);
}