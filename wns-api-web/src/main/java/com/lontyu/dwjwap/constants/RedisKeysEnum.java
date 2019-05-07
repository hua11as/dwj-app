package com.lontyu.dwjwap.constants;

public enum RedisKeysEnum {

    WX_ACCESS_TOKEN(1,"db:wap:access_token","微信access_token KEY",7200L),
    WX_MOBILE_VERIFY_CODE(2,"db:wap:mobileVerify","短信验证码",300L),
    SYSTEM_ODDS_CONFIG(3,"db:wap:odds_config","系统赔率配置",300L),
    LOGIN_VERIFY_CODE(4,"db:wap:loginVerify","短信验证码",300L);

    private int id;
    private String key;
    private String desc;
    private long expireIn;

    private RedisKeysEnum(int id,String key,String desc,Long expireIn){
        this.id = id;
        this.key = key;
        this.desc = desc;
        this.expireIn = expireIn;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public long getExpireIn() {
        return expireIn;
    }
}
