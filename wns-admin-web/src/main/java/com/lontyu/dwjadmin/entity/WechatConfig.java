package com.lontyu.dwjadmin.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@TableName("t_wechat_config")
public class WechatConfig implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;

    @NotBlank(message = "appId 不能为空",groups = UpdateGroup.class)
    private String appId;

    @NotBlank(message = "appSecret 不能为空",groups = UpdateGroup.class)
    private String appSecret;

    private Integer status;

    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret == null ? null : appSecret.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}