package com.lontyu.dwjadmin.constants;

/**
 * Created by Cory on 2018/10/9.
 */
public enum MoneyRecordTypeEnum {

    // 1充值，2提现，3赚入，4下注，5佣金, 6平台兑付, 7平台下注收入, 8平台佣金支出, 9提现申请, 10提现退回
    RECHARGE(1, "充值"), WITHDRAW(2, "提现"), EARN(3, "赚入"), CHIP_IN(4, "下注"),
    COMMISSION(5, "佣金"), PLATFORM_PAY_OUT(6, "平台开奖兑付"), PLATFORM_CHIP_IN(7, "平台下注收入"),
    PLATFORM_PAY_COMMISSION(8, "平台佣金支出"), WITHDRAW_APPLY(9, "提现申请"), WITHDRAW_RETURN(10, "提现退回");

    private int code;
    private String desc;

    MoneyRecordTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MoneyRecordTypeEnum keyOf(Integer code) {
        if (null == code) {
            return null;
        }

        for (MoneyRecordTypeEnum moneyRecordTypeEnum : values()) {
            if (moneyRecordTypeEnum.code == code) {
                return moneyRecordTypeEnum;
            }
        }

        return null;
    }
}
