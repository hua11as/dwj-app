package com.lontyu.dwjwap.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CookieUtil {

    private static Logger logger = LoggerFactory.getLogger(CookieUtil.class);
    private static String secretkey = "";

    static {
        secretkey = ((Environment) SpringUtil.getBean(Environment.class)).getProperty("cookie.secretkey");
        if(secretkey == null || "".equals(secretkey)){
            secretkey = "dWj123~";
        }
    }

    /**
     * 从cookie中取值
     *
     * @param key
     * @param decrypt
     * @return
     */
    public static String getCookie(String key, boolean decrypt) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        Cookie cookie = WebUtils.getCookie(request, key);
        if (cookie != null) {
            String ckValue = cookie.getValue();
            try {
                ckValue = URLDecoder.decode(ckValue, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                logger.error(e1.getMessage());
            }
            if (decrypt) {
                ckValue = Cryptography.TripleDESDecrypt(ckValue, secretkey);
            }
            return ckValue;
        }
//        logger.info("获取的cookies:{},值为{}", key, Objects.isNull(cookie) ? null : cookie.getValue());
        return null;
    }

    /**
     * 设置cookies
     *
     * @param key
     * @param value
     * @param enCrypt
     */
    public static String setCookie(String key, String value, boolean enCrypt) {
        return setCookie(null, key, value, enCrypt, null);
    }

    public static String setCookie(String key, String value, boolean enCrypt, Integer maxAge) {
        return setCookie(null, key, value, enCrypt, maxAge);
    }

    public static String setCookie(String domain, String key, String value, boolean enCrypt, Integer maxAge) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse resp = attrs.getResponse();
        try {
//			String jsonVal = JSON.toJSONString(value);
            if (enCrypt) {
                value = Cryptography.TripleDESEncrypt(value, secretkey);
            }
            value = URLEncoder.encode(value, "UTF-8");
            Cookie cookie = new Cookie(key, value);
            cookie.setPath("/");
            if (domain != null && !"".equals(domain)) {
                cookie.setDomain(domain);
            }
            if (maxAge != null) {
                cookie.setMaxAge(maxAge);
            }
            resp.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error("setCookie(HttpServletResponse resp, String key,T value,boolean encode)", e);
        }
        return value;
    }

    /**
     * 清空当前域的指定的cookie
     *
     * @param key
     */
    public static void clearCookie(String key) {
        clearCookie(null, key);
    }

    public static void clearCookie(String domain, String key) {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse resp = attrs.getResponse();
        Cookie cookie = new Cookie(key, null);
        if (domain != null && !"".equals(domain)) {
            cookie.setDomain(domain);
        }
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

    public static String getHost(HttpServletRequest request,String defaultHost){
        String host=null;
        if((host=request.getServerName()).matches("^[^.]*[.][^.]*[.][^.]*$")){
            host=request.getServerName()+":"+request.getServerPort();
        }else if((host=request.getHeader("Host")).matches("^[^.]*[.][^.]*[.][^.:]*[:]?[0-9]{0,5}$")){
            host=request.getHeader("Host");
        }else{
            host=defaultHost;
        }
        return host;
    }
}
