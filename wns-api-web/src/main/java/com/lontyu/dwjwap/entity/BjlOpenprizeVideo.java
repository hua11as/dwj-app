package com.lontyu.dwjwap.entity;

import java.util.Date;

public class BjlOpenprizeVideo {
    private Integer id;

    private Integer totalPlayTimes;

    private Integer resultSign;

    private Integer status;

    private String playerPoint;

    private String bankerPoint;

    private String linkAdress;

    private Date addTime;

    private String remark;

    private Integer orderTimes;

    private Integer calOrderTimes;

    private Integer playTimes;

    private Integer showResultTimes;

    //闲家牌颜色（1，2，3，4 表示黑红梅方）
    private String playerPointColor;
    //庄家牌颜色（1，2，3，4 表示黑红梅方）
    private String bankerPointColor;

    private Integer deingerNum;

    private Integer playerPair;
    private Integer bankerPair;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalPlayTimes() {
        return totalPlayTimes;
    }

    public void setTotalPlayTimes(Integer totalPlayTimes) {
        this.totalPlayTimes = totalPlayTimes;
    }

    public Integer getResultSign() {
        return resultSign;
    }

    public void setResultSign(Integer resultSign) {
        this.resultSign = resultSign;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPlayerPoint() {
        return playerPoint;
    }

    public void setPlayerPoint(String playerPoint) {
        this.playerPoint = playerPoint == null ? null : playerPoint.trim();
    }

    public String getBankerPoint() {
        return bankerPoint;
    }

    public void setBankerPoint(String bankerPoint) {
        this.bankerPoint = bankerPoint == null ? null : bankerPoint.trim();
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

    public Integer getOrderTimes() {
        return orderTimes;
    }

    public void setOrderTimes(Integer orderTimes) {
        this.orderTimes = orderTimes;
    }

    public Integer getCalOrderTimes() {
        return calOrderTimes;
    }

    public void setCalOrderTimes(Integer calOrderTimes) {
        this.calOrderTimes = calOrderTimes;
    }

    public Integer getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(Integer playTimes) {
        this.playTimes = playTimes;
    }

    public Integer getShowResultTimes() {
        return showResultTimes;
    }

    public void setShowResultTimes(Integer showResultTimes) {
        this.showResultTimes = showResultTimes;
    }

    public String getPlayerPointColor() {
        return playerPointColor;
    }

    public void setPlayerPointColor(String playerPointColor) {
        this.playerPointColor = playerPointColor;
    }

    public String getBankerPointColor() {
        return bankerPointColor;
    }

    public void setBankerPointColor(String bankerPointColor) {
        this.bankerPointColor = bankerPointColor;
    }

    public Integer getDeingerNum() {
        return deingerNum;
    }

    public void setDeingerNum(Integer deingerNum) {
        this.deingerNum = deingerNum;
    }

    public Integer getPlayerPair() {
        return playerPair;
    }

    public void setPlayerPair(Integer playerPair) {
        this.playerPair = playerPair;
    }

    public Integer getBankerPair() {
        return bankerPair;
    }

    public void setBankerPair(Integer bankerPair) {
        this.bankerPair = bankerPair;
    }
}