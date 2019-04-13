package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/5 20:23
 */
@Getter
@Setter
@TableName("t_wechat_member")
public class WechatMember{
    @TableId
    private Integer id;

    private String openId;

    private String nickName;

    private Integer vipId;

    private String headImg;

    private Integer weconfigId;

    private Date synDate;

    private Integer inviterId;

    private Date createTime;
}
