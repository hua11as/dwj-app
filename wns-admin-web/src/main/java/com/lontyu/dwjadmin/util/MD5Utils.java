package com.lontyu.dwjadmin.util;

import java.security.MessageDigest;

public class MD5Utils {
	/**
	 * 使用md5的算法进行加密
	 */
	public static String md5(String src) {
		try {
			StringBuffer buffer = new StringBuffer();
			MessageDigest messageDigest = MessageDigest.getInstance("md5");
			char[] ch = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'A', 'B', 'C', 'D', 'E', 'F' };
			byte[] b = messageDigest.digest(src.getBytes());

			for (int i = 0; i < b.length; i++) {
				// 将高4位转换成字符串
				int x = (b[i] >>> 4 & 0x0f);
				buffer.append(ch[x]);
				// 将低4位转换成字符串
				x = (b[i] & 0x0f);
				buffer.append(ch[x]);
			}
			return buffer.toString();
		} catch (Exception e) {
		}
		return null;
	}

}
