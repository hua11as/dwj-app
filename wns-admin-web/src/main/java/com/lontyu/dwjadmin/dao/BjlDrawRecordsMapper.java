package com.lontyu.dwjadmin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.BjlDrawRecords;
import org.apache.ibatis.annotations.Param;

public interface BjlDrawRecordsMapper extends BaseMapper<BjlDrawRecords> {
    int deleteByPrimaryKey(Integer id);

    Integer insert(BjlDrawRecords record);

    int insertSelective(BjlDrawRecords record);

    BjlDrawRecords selectByPrimaryKey(Integer id);

    BjlDrawRecords selectByAwardPeriod(@Param("awardPeriod") String awardPeriod);

    int updateByPrimaryKeySelective(BjlDrawRecords record);

    int updateByPrimaryKey(BjlDrawRecords record);

    BjlDrawRecords selectActiveRecordForUpdate();

}