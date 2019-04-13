package com.lontyu.dwjwap.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO 类的描述
 *
 * @author 陈达锋
 * @createTime 2018-07-20 09:59:25
 */
public class OrderVo  {

    private String id;



    private String password;



    private String userId;

    private BigDecimal amount;

    private Integer productId;

    private Integer isVerify;

    private Integer isWin;

    private String productName;

    private Integer drawIssue;

    private Integer isPay;

    private BigDecimal winAmount;

    private Date createDate;

    private String openDrawCode;
    List<OrderItemVo> orderItemVos = new ArrayList<OrderItemVo>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(Integer isVerify) {
        this.isVerify = isVerify;
    }

    public Integer getIsWin() {
        return isWin;
    }

    public void setIsWin(Integer isWin) {
        this.isWin = isWin;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getDrawIssue() {
        return drawIssue;
    }

    public void setDrawIssue(Integer drawIssue) {
        this.drawIssue = drawIssue;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOpenDrawCode() {
        return openDrawCode;
    }

    public void setOpenDrawCode(String openDrawCode) {
        this.openDrawCode = openDrawCode;
    }

    public List<OrderItemVo> getOrderItemVos() {
        return orderItemVos;
    }

    public void setOrderItemVos(List<OrderItemVo> orderItemVos) {
        this.orderItemVos = orderItemVos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
