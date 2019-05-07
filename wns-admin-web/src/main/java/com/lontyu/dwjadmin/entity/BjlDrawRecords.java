package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

@TableName("bjl_draw_records")
public class BjlDrawRecords {
    private Integer id;

    private String awardPeriod;

    private Integer drawResult;

    private Integer awardVideo;

    private Integer statisticalMethod;

    private String remark;

    private Date startOrderTime;

    private Date endOrderTime;

    private Date startWaitPlayTime;

    private Date endWaitPlayTime;

    private Date startPlayTime;

    private Date endPlayTime;

    private Date startShowResultTime;

    private Date endShowResultTime;

    @TableField(value = "pre_video_1")
    private Integer preVideo1;

    @TableField(value = "pre_video_2")
    private Integer preVideo2;

    private Integer status;

    private Integer forceTie;

    private Date addTime;

    private Date updateTime;

    private Integer delFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAwardPeriod() {
        return awardPeriod;
    }

    public void setAwardPeriod(String awardPeriod) {
        this.awardPeriod = awardPeriod == null ? null : awardPeriod.trim();
    }

    public Integer getDrawResult() {
        return drawResult;
    }

    public void setDrawResult(Integer drawResult) {
        this.drawResult = drawResult;
    }

    public Integer getAwardVideo() {
        return awardVideo;
    }

    public void setAwardVideo(Integer awardVideo) {
        this.awardVideo = awardVideo;
    }

    public Integer getStatisticalMethod() {
        return statisticalMethod;
    }

    public void setStatisticalMethod(Integer statisticalMethod) {
        this.statisticalMethod = statisticalMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getStartOrderTime() {
        return startOrderTime;
    }

    public void setStartOrderTime(Date startOrderTime) {
        this.startOrderTime = startOrderTime;
    }

    public Date getEndOrderTime() {
        return endOrderTime;
    }

    public void setEndOrderTime(Date endOrderTime) {
        this.endOrderTime = endOrderTime;
    }

    public Date getStartWaitPlayTime() {
        return startWaitPlayTime;
    }

    public void setStartWaitPlayTime(Date startWaitPlayTime) {
        this.startWaitPlayTime = startWaitPlayTime;
    }

    public Date getEndWaitPlayTime() {
        return endWaitPlayTime;
    }

    public void setEndWaitPlayTime(Date endWaitPlayTime) {
        this.endWaitPlayTime = endWaitPlayTime;
    }

    public Date getStartPlayTime() {
        return startPlayTime;
    }

    public void setStartPlayTime(Date startPlayTime) {
        this.startPlayTime = startPlayTime;
    }

    public Date getEndPlayTime() {
        return endPlayTime;
    }

    public void setEndPlayTime(Date endPlayTime) {
        this.endPlayTime = endPlayTime;
    }

    public Date getStartShowResultTime() {
        return startShowResultTime;
    }

    public void setStartShowResultTime(Date startShowResultTime) {
        this.startShowResultTime = startShowResultTime;
    }

    public Date getEndShowResultTime() {
        return endShowResultTime;
    }

    public void setEndShowResultTime(Date endShowResultTime) {
        this.endShowResultTime = endShowResultTime;
    }

    public Integer getPreVideo1() {
        return preVideo1;
    }

    public void setPreVideo1(Integer preVideo1) {
        this.preVideo1 = preVideo1;
    }

    public Integer getPreVideo2() {
        return preVideo2;
    }

    public void setPreVideo2(Integer preVideo2) {
        this.preVideo2 = preVideo2;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getForceTie() {
        return forceTie;
    }

    public void setForceTie(Integer forceTie) {
        this.forceTie = forceTie;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}