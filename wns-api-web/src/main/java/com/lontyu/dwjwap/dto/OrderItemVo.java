package com.lontyu.dwjwap.dto;


import java.math.BigDecimal;

/**
 * TODO 类的描述
 *
 * @author
 * @createTime 2018-07-20 10:06:26
 */
public class OrderItemVo {


    private String id;

    private Integer orderId;

    private String drawCode;

    private Float lossPerCent;

    private BigDecimal price;

    private Integer num;

    private Integer isWin;

    private Integer type;

    private Integer drawType;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDrawType() {
        return drawType;
    }

    public void setDrawType(Integer drawType) {
        this.drawType = drawType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getDrawCode() {
        return drawCode;
    }

    public void setDrawCode(String drawCode) {
        this.drawCode = drawCode;
    }

    public Float getLossPerCent() {
        return lossPerCent;
    }

    public void setLossPerCent(Float lossPerCent) {
        this.lossPerCent = lossPerCent;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getIsWin() {
        return isWin;
    }

    public void setIsWin(Integer isWin) {
        this.isWin = isWin;
    }
}
