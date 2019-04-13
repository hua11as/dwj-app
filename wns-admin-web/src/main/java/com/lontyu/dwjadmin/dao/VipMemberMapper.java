package com.lontyu.dwjadmin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.lontyu.dwjadmin.entity.VipMember;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface VipMemberMapper extends BaseMapper<VipMember>{

    int deleteByPrimaryKey(Integer id);

    VipMember selectByPrimaryKey(Integer id);

    VipMember selectByPrimaryKeyForUpdate(Integer id);

    int updateByPrimaryKey(VipMember record);

    int updateAoumt(Map<String,Object> map);

    int countNum(@Param("countDate") String countDate);

    List<Map<String,Object>> countList();

    BigDecimal sumAmountWithoutPlatform();
}