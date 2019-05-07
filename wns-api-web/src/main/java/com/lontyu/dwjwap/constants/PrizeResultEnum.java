package com.lontyu.dwjwap.constants;

import com.lontyu.dwjwap.exception.BizException;

/**
 * Created by Cory on 2018/10/8.
 */
public enum PrizeResultEnum {
     XWin(1,"闲胜"),ZWin(2,"庄胜"), HE(3,"和");

    private int code;
    private String desc;

    PrizeResultEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static PrizeResultEnum codeOf(int code) {
        for (PrizeResultEnum prizeResultEnum : values()) {
            if (code == prizeResultEnum.getCode()) {
                return prizeResultEnum;
            }
        }

        throw new BizException("开奖结果不正确");
    }
}
