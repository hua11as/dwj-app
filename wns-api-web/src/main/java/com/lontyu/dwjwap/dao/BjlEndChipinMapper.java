package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.BjlEndChipin;

public interface BjlEndChipinMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(BjlEndChipin record);

    BjlEndChipin selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(BjlEndChipin record);

    BjlEndChipin selectAllByObject(BjlEndChipin endChipin);
}