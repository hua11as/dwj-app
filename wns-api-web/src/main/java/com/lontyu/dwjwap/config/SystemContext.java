package com.lontyu.dwjwap.config;

import com.lontyu.dwjwap.entity.WechatMember;

/**
 * 应用的容器
 * 
 */
public class SystemContext {
	private static ThreadLocal<Integer> userHolder = new ThreadLocal<Integer>();

	public static void setCurrentUser(Integer id) {
		userHolder.set(id);
	}

	public static Integer getCurrentUser() {
		return userHolder.get();
	}

	public static void clearCurrentUser() {
		userHolder.remove();
	}
	
}
