package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.BjlDrawRecords;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BjlDrawRecordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BjlDrawRecords record);

    int insertSelective(BjlDrawRecords record);

    BjlDrawRecords selectByPrimaryKey(Integer id);

    BjlDrawRecords selectByAwardPeriod(@Param("awardPeriod") String awardPeriod);

    int updateByPrimaryKeySelective(BjlDrawRecords record);

    int updateByPrimaryKey(BjlDrawRecords record);

    BjlDrawRecords selectActiveRecord();


    BjlDrawRecords selectActiveRecordForUpdate();

    List<BjlDrawRecords> selectTrendChartData();
}