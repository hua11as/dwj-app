package com.lontyu.dwjadmin.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.BjlOpenprizeVideo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BjlOpenprizeVideoMapper extends BaseMapper<BjlOpenprizeVideo> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BjlOpenprizeVideo record);

    BjlOpenprizeVideo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BjlOpenprizeVideo record);

    int updateByPrimaryKey(BjlOpenprizeVideo record);

    BjlOpenprizeVideo selectRandomVideo(@Param("resultSign") int resultSign);
}