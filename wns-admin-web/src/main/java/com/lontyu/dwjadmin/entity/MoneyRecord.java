package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lontyu.dwjadmin.common.validator.group.AddGroup;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;
import io.swagger.models.auth.In;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("t_money_record")
public class MoneyRecord  implements Serializable{



    @TableId
    private Integer id;

    @TableField(value = "vip_id")
    private Integer vipId;

    @NotNull(message = "交易金额",groups = {AddGroup.class,UpdateGroup.class})
    private BigDecimal amount;

    @NotNull(message = "交易类型",groups = {AddGroup.class,UpdateGroup.class})
    private Integer type;

    private String remark;

    private Integer status;
    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "create_time")
    private Date createTime;

    private String image;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}