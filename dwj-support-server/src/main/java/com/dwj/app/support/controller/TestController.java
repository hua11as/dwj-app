package com.dwj.app.support.controller;

import com.dwj.app.support.util.ThirdWithdrawUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author as
 * @desc
 * @sinse 2019/3/29
 */
@Component
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/thirdWithdraw")
    @ResponseBody
    public String testThirdWithdraw() {
        return ThirdWithdrawUtils.thirdWithdrawTest();
    }
}
