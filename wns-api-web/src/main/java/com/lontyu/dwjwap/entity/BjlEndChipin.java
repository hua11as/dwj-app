package com.lontyu.dwjwap.entity;

import java.util.Date;

public class BjlEndChipin {
    private Integer id;

    private String videoSerial;

    private Integer playOrder;

    private Integer currentPeriods;

    private Date endChipinTime;

    private Integer currentResult;

    private String playerPoint;

    private String bankerPoint;

    private String linkAdress;

    private Date addTime;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVideoSerial() {
        return videoSerial;
    }

    public void setVideoSerial(String videoSerial) {
        this.videoSerial = videoSerial == null ? null : videoSerial.trim();
    }

    public Integer getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(Integer playOrder) {
        this.playOrder = playOrder;
    }

    public Integer getCurrentPeriods() {
        return currentPeriods;
    }

    public void setCurrentPeriods(Integer currentPeriods) {
        this.currentPeriods = currentPeriods;
    }

    public Date getEndChipinTime() {
        return endChipinTime;
    }

    public void setEndChipinTime(Date endChipinTime) {
        this.endChipinTime = endChipinTime;
    }

    public Integer getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(Integer currentResult) {
        this.currentResult = currentResult;
    }

    public String getPlayerPoint() {
        return playerPoint;
    }

    public void setPlayerPoint(String playerPoint) {
        this.playerPoint = playerPoint;
    }

    public String getBankerPoint() {
        return bankerPoint;
    }

    public void setBankerPoint(String bankerPoint) {
        this.bankerPoint = bankerPoint;
    }

    public String getLinkAdress() {
        return linkAdress;
    }

    public void setLinkAdress(String linkAdress) {
        this.linkAdress = linkAdress == null ? null : linkAdress.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}