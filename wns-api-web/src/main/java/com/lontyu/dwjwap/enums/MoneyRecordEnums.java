package com.lontyu.dwjwap.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription: 流水类型枚举
 * @author: as
 * @date: 2018/10/18 23:42
 */
public final class MoneyRecordEnums {

    @Getter
    @AllArgsConstructor
    public static enum MoneyRecordTypeEnum{
        // 1充值，2提现，3赚入，4下注，5佣金
        RECHARGE(1, "充值", MoneyRecordInOrOut.IN),
        WITHDRAW(2, "提现", MoneyRecordInOrOut.OUT),
        BONUS(3, "奖金", MoneyRecordInOrOut.IN),
        STAKE(4, "下注金额", MoneyRecordInOrOut.OUT),
        COMMISSION(5, "佣金", MoneyRecordInOrOut.IN),
        PLATFORM_PAY_OUT(6,"平台开奖兑付", MoneyRecordInOrOut.OUT),
        PLATFORM_CHIP_IN(7,"平台下注收入", MoneyRecordInOrOut.IN),
        PLATFORM_PAY_COMMISSION(8,"平台佣金支出", MoneyRecordInOrOut.OUT),
        WITHDRAW_APPLY(9, "提现申请", MoneyRecordInOrOut.OUT),
        WITHDRAW_RETURN(10, "提现退回", MoneyRecordInOrOut.IN);
        private int code;
        private String desc;
        private MoneyRecordInOrOut inOrOut;
    }

    @Getter
    @AllArgsConstructor
    public static enum MoenyRecordStatus{
        // 交易状态（0待入账，1成功，2失败）
        WAIT(0, "待入帐"),
        SUCCESS(1, "成功"),
        FAIL(2, "失败");
        private int code;
        private String desc;
    }

    @Getter
    public static enum MoneyRecordInOrOut {
        IN,OUT
    }
}
