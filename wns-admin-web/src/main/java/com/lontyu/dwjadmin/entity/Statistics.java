package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.math.BigDecimal;

/**
 * @author as
 * @desc
 * @date 2019/1/22
 */
@TableName("t_statistics")
public class Statistics {
    @TableId(type = IdType.INPUT)
    private String statDate;
    private Integer registerNum = 0;
    private BigDecimal recharge = BigDecimal.ZERO;
    private BigDecimal withdraw = BigDecimal.ZERO;
    private BigDecimal bet = BigDecimal.ZERO;
    private BigDecimal compensate = BigDecimal.ZERO;
    private BigDecimal commission = BigDecimal.ZERO;
    private BigDecimal morePoints = BigDecimal.ZERO;
    private BigDecimal bunko = BigDecimal.ZERO;

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public Integer getRegisterNum() {
        return registerNum;
    }

    public void setRegisterNum(Integer registerNum) {
        this.registerNum = registerNum;
    }

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public BigDecimal getBet() {
        return bet;
    }

    public void setBet(BigDecimal bet) {
        this.bet = bet;
    }

    public BigDecimal getCompensate() {
        return compensate;
    }

    public void setCompensate(BigDecimal compensate) {
        this.compensate = compensate;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getMorePoints() {
        return morePoints;
    }

    public void setMorePoints(BigDecimal morePoints) {
        this.morePoints = morePoints;
    }

    public BigDecimal getBunko() {
        return bunko;
    }

    public void setBunko(BigDecimal bunko) {
        this.bunko = bunko;
    }
}
