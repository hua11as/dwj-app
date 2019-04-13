package com.lontyu.dwjwap.config.interceptor;

import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.config.SystemContext;
import com.lontyu.dwjwap.utils.CookieUtil;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    GlobalsConfig config;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String url = request.getServletPath();
        if (isNeedLogin(url)) {
            String cookie = CookieUtil.getCookie("dwjCookie", true);
            if (cookie != null) {
                String[] str = cookie.split("_");
                String id = str[0];
                if (id != null) {
                    SystemContext.setCurrentUser(Integer.parseInt(id));
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        StringManager.getManager(AuthenticatorBase.class).getString("authenticator.unauthorized"));
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        SystemContext.clearCurrentUser();
    }


    private boolean isNeedLogin(String path) {
        if (path.equals("/")) {
            return false;
        } else if (path.startsWith("/weChat/getOpenId") || path.startsWith("/wx")
                || path.startsWith("/wxPay") || path.startsWith("/login")
                || path.startsWith("/webjars") || path.startsWith("/swagger-resources")
                || path.startsWith("/ucenter/intro") || path.startsWith("/callback")) {
            return false;
        } else {
            return true;
        }
    }


}
