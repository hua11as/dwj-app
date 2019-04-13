package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.constants.PrizeResultEnum;
import com.lontyu.dwjwap.entity.BjlOpenprizeVideo;
import org.apache.ibatis.annotations.Param;

public interface BjlOpenprizeVideoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BjlOpenprizeVideo record);

    int insertSelective(BjlOpenprizeVideo record);

    BjlOpenprizeVideo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BjlOpenprizeVideo record);

    int updateByPrimaryKey(BjlOpenprizeVideo record);

    BjlOpenprizeVideo selectRandomVideo(@Param("resultSign") Integer resultSign,
                                        @Param("deingerNum") Integer deingerNum);

    int selectNextDeingerNum(@Param("deingerNum") Integer deingerNum);

    BjlOpenprizeVideo selectNextVideo(@Param("deingerNum") Integer deingerNum,
                                      @Param("id") Integer id);

    BjlOpenprizeVideo selectMappingVideo(@Param("id") Integer id, @Param("deingerNum") Integer deingerNum,
                                         @Param("resultSign") Integer resultSign, @Param("playerPair") Integer playerPair,
                                         @Param("bankerPair") Integer bankerPair);
}