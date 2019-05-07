package com.lontyu.dwjwap.utils.thirdPay;

import java.security.MessageDigest;

/**
 * @author as
 * @desc
 * @sinse 2019/4/11
 */
public class SHA1 {
    public static String encrypt(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data.getBytes("UTF-8"));
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte message : messageDigest) {
                String shaHex = Integer.toHexString(message & 0xFF);
                if (shaHex.length() < 2)
                    hexString.append(0);

                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
