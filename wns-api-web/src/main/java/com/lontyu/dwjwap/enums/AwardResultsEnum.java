package com.lontyu.dwjwap.enums;

/**
 * 开奖结果枚举
 * Created by Cory on 2018/9/2.
 */
public enum AwardResultsEnum {
    UNKNOWN(0,"未知"),BANKER_WIN(1,"庄家胜"),CLIENT_WIN(2,"闲家胜");
    private Integer code;
    private String desc;
    AwardResultsEnum(Integer code,String desc){
        this.code=code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
