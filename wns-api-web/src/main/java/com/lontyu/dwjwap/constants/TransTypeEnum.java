package com.lontyu.dwjwap.constants;

import com.lontyu.dwjwap.enums.MoneyRecordEnums;

/**
 * 交易类型枚举
 */
public enum TransTypeEnum {

    // 1充值，2提现，3赚入，4下注，5佣金, 6平台兑付, 7平台下注收入, 8平台佣金支出
    RECHARGE(1, "充值"),
    WITHDRAW(2, "提现"),
    EARN_MONEY(3, "赚入"),
    CHIP_IN(4, "下注"),
    COMMISSION(5, "佣金"),
    PLATFORM_PAY_OUT(6,"平台开奖兑付"),
    PLATFORM_CHIP_IN(7,"平台下注收入"),
    PLATFORM_PAY_COMMISSION(8,"平台佣金支出"),
    WITHDRAW_APPLY(9, "提现申请"),
    WITHDRAW_RETURN(10, "提现退回");

    private int type;

    private String name;

    private TransTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static TransTypeEnum getObjByType(int type) {
        TransTypeEnum[] values = values();
        for (TransTypeEnum t : values) {
            if (type == t.getType()) {
                return t;
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
