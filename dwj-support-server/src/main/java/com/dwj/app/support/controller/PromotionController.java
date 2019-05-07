package com.dwj.app.support.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author as
 * @desc 推广服务
 * @sinse 2019/3/27
 */
@Component
@RequestMapping("/promotion")
public class PromotionController {

    @GetMapping("getUrl")
    public ModelAndView getUrl(String flag, Integer inviterId, HttpServletRequest request) {
        String promotionUrl = getPromotionUrl(flag, inviterId);
        return new ModelAndView("redirect:" + promotionUrl);
    }

    private String getPromotionUrl(String flag, Integer inviterId) {
        Map<String, String> urlMap = new HashMap<>(5);
        urlMap.put("dwj", "http://dwj01.bsgbwl.com");
        urlMap.put("wls", "http://play.i0jc9.cn");

        return Optional.ofNullable(urlMap.get(flag)).orElse(urlMap.get("dwj")) + "?inviterId=" + inviterId;
    }
}
