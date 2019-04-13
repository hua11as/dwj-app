package com.lontyu.dwjadmin.constants;

public enum SysParamEnum {
    XIAN(1, "闲"),
    XIANDUI(2, "闲对"),
    HE(3, "和"),
    ZHUANGDUI(4, "庄对"),
    ZHUANG(5, "庄");

    private int code;
    private String name;

    SysParamEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public static SysParamEnum getSelectedSizeEnum(Integer code){
        for(SysParamEnum en: SysParamEnum.values()){
            if(en.getCode()==code){
                return en;
            }
        }
        return null;

    }
}
