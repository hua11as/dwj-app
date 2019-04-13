package com.lontyu.dwjwap.utils;


import java.util.Random;

public class NumberUtils {

    /**
     *  随机生成6位验证码
     * @return
     */
    public static String create6Num(){
        Random random = new Random();
        String result="";
        for (int i=0;i<6;i++)
        {
            result+=random.nextInt(10);
        }

        return result;
    }
}
