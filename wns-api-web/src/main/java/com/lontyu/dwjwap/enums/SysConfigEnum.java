package com.lontyu.dwjwap.enums;

/**
 * 系统配置
 * Created by Cory on 2018/9/2.
 */
public enum SysConfigEnum {
    SYS_STATUS("SYS_STATUS","系统状态 0：歇业，1：开业"),
    ORDER_TIME("ORDER_TIME","下注时长"),
    SHOW_RESULT_TIME("SHOW_RESULT_TIME","开奖结果显示时长");
    private String key;
    private String desc;

    SysConfigEnum(String key,String desc){
        this.key=key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
