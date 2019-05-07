package com.lontyu.dwjwap.entity;

import java.util.Date;

public class WechatMember {
    private Integer id;

    private String openId;

    private String nickName;

    private Integer vipId;

    private String headImg;

    private Integer weconfigId;

    private Date synDate;

    private Integer inviterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg == null ? null : headImg.trim();
    }

    public Integer getWeconfigId() {
        return weconfigId;
    }

    public void setWeconfigId(Integer weconfigId) {
        this.weconfigId = weconfigId;
    }

    public Date getSynDate() {
        return synDate;
    }

    public void setSynDate(Date synDate) {
        this.synDate = synDate;
    }

    public Integer getInviterId() {
        return inviterId;
    }

    public void setInviterId(Integer inviterId) {
        this.inviterId = inviterId;
    }

    @Override
    public String toString() {
        return "WechatMember{" +
                "id=" + id +
                ", openId='" + openId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", vipId=" + vipId +
                ", headImg='" + headImg + '\'' +
                ", weconfigId=" + weconfigId +
                ", synDate=" + synDate +
                ", inviterId=" + inviterId +
                '}';
    }
}