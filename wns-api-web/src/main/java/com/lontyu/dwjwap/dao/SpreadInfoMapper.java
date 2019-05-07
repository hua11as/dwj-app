package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.SpreadInfo;

public interface SpreadInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpreadInfo record);

    SpreadInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpreadInfo record);

    int updateByPrimaryKey(SpreadInfo record);

    String selectUrlByUserId(Integer userId);
}