package com.lontyu.dwjwap.dao;

import com.lontyu.dwjwap.entity.WechatMember;

import java.util.List;
import java.util.Map;

public interface WechatMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WechatMember record);

    WechatMember selectByPrimaryKey(Integer id);

    WechatMember selectByVipId(Integer vipId);

    int updateByPrimaryKey(WechatMember record);

    WechatMember getWeChatMemberByOpenId(Map<String, String> params);

    List<WechatMember> selectNoSysnData();

    int sysnUpdateInfo(WechatMember record);

    WechatMember selectByOpenId(String vipId);
}