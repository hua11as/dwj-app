package com.lontyu.dwjwap.constants;

public enum SelectedSizeEnum {
    XIAN(1, "闲"),
    XIANDUI(2, "闲对"),
    HE(3, "和"),
    ZHUANGDUI(4, "庄对"),
    ZHUANG(5, "庄");

    private int code;
    private String name;

    SelectedSizeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public static SelectedSizeEnum getSelectedSizeEnum(Integer code){
        for(SelectedSizeEnum en: SelectedSizeEnum.values()){
            if(en.getCode()==code){
                return en;
            }
        }
        return null;

    }

}
