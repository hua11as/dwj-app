package com.lontyu.dwjadmin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.MoneyRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface MoneyRecordMapper extends BaseMapper<MoneyRecord> {

    BigDecimal selectAmountByType(Map<String,Object> map);

    List<Map<String,Object>> countByDate(@Param("minDate") String minDate, @Param("maxDate") String maxDate);

    List<Map<String,Object>> statByDate(@Param("statDate") String statDate);

    List<Map<String,Object>> countAll();
}