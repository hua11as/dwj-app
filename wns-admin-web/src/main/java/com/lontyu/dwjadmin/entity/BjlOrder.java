package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.math.BigDecimal;
import java.util.Date;

@TableName("bjl_order")
public class BjlOrder {
    @TableId
    private Integer id;

    @TableField(value = "vip_id")
    private Integer vipId;

    private String periods;

    @TableField(value = "buy_amount")
    private BigDecimal buyAmount;

    @TableField(value = "support_win")
    private Integer supportWin;

    @TableField(value = "selected_size")
    private Integer selectedSize;

    @TableField(value = "video_serial")
    private String videoSerial;

    @TableField(value = "final_result")
    private Integer finalResult;

    @TableField(value = "count_commission")
    private Integer countCommission;

    @TableField(value = "add_time")
    private Date addTime;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public Integer getSupportWin() {
        return supportWin;
    }

    public void setSupportWin(Integer supportWin) {
        this.supportWin = supportWin;
    }

    public Integer getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(Integer selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getVideoSerial() {
        return videoSerial;
    }

    public void setVideoSerial(String videoSerial) {
        this.videoSerial = videoSerial == null ? null : videoSerial.trim();
    }

    public Integer getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(Integer finalResult) {
        this.finalResult = finalResult;
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

    public Integer getCountCommission() {
        return countCommission;
    }

    public void setCountCommission(Integer countCommission) {
        this.countCommission = countCommission;
    }
}