package com.lontyu.dwjwap.controller;

import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.dao.WechatConfigMapper;
import com.lontyu.dwjwap.entity.WechatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 *  主页控制器
 */

@Controller
public class IndexController {

    @Autowired
    private WechatConfigMapper wechatConfigMapper;

    @Autowired
    GlobalsConfig config;

    /**
     * 微信授权之后，进入大玩家首页接口
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index(HttpServletRequest request, Model model) {
        Integer configId = config.getDefaultConfigId();

        WechatConfig weChatConfig = wechatConfigMapper.selectByPrimaryKey(configId);
        model.addAttribute("wxAppId", weChatConfig.getAppId());
        model.addAttribute("configid", configId);
        String inviterId = request.getParameter("inviterId"); // 推荐id
        if (inviterId != null) {
            model.addAttribute("inviterId", inviterId);
        }else{
            model.addAttribute("inviterId", 0);
        }
        return new ModelAndView("page/pages/index/index", "userModel", model);
    }

}
