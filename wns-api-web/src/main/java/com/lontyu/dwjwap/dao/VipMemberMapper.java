package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.VipMember;

import java.util.List;
import java.util.Map;

public interface VipMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VipMember record);

    VipMember selectByPrimaryKey(Integer id);

    VipMember selectByPrimaryKeyForUpdate(Integer id);

    int updateByPrimaryKey(VipMember record);

    VipMember getVipMemberByMobile(String mobile);

    int updateAmountByVesion(Map<String,Object> params);

    List<VipMember> selectByInviterIds(List<Integer> inviterIds);
}