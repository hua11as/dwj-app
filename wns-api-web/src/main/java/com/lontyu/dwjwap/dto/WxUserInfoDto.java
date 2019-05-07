package com.lontyu.dwjwap.dto;

public class WxUserInfoDto {
//
//    "subscribe": 1,
//    "openid": "otvxTs4dckWG7imySrJd6jSi0CWE",
//    "nickname": "iWithery",
//    "sex": 1,
//    "language": "zh_CN",
//    "city": "揭阳",
//    "province": "广东",
//    "country": "中国",
//
//    "headimgurl": "http://thirdwx.qlogo.cn/mmopen/xbIQx1GRqdvyqkMMhEaGOX802l1CyqMJNgUzKP8MeAeHFicRDSnZH7FY4XB7p8XHXIf6uJA2SCunTPicGKezDC4saKISzRj3nz/0",
//
//    "subscribe_time": 1434093047,
//    "unionid": "oR5GjjgEhCMJFyzaVZdrxZ2zRRF4",
//    "remark": "",
//
//    "groupid": 0,
//    "tagid_list":[128,2],
//    "subscribe_scene": "ADD_SCENE_QR_CODE",
//    "qr_scene": 98765,
//    "qr_scene_str": ""

    private String openid;

    private String nickname;

    private String headimgurl;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public String toString() {
        return "WxUserInfoDto{" +
                "openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                '}';
    }
}
