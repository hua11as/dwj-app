package com.lontyu.dwjwap.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Cory on 2018/10/17.
 */
@Controller
@Api(value = "RecordController", tags = "页面跳转控制器")
public class RecordController {

    /**
     * 投注记录页面
     * @return
     */
    @RequestMapping("/record")
    @ApiOperation("投注记录页面")
    public ModelAndView payRecord(HttpServletRequest request, Model model) {

        return new ModelAndView("page/pages/pay-record/pay-record", "userModel", model);
    }

    /**
     * 投注记录详情页面
     * @return
     */
    @RequestMapping("/record/detail/bjl")
    @ApiOperation("投注记录详情页面")
    public ModelAndView recordDetail(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/pay-record/detail-bjl", "userModel", model);
    }

    /**
     *充值记录
     * @return
     */
    @RequestMapping("/tradingRecord/pay")
    @ApiOperation("充值记录")
    public ModelAndView tradingRecord(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/trading-record/pay", "userModel", model);
    }
    /**
     *提现记录
     * @return
     */
    @RequestMapping("/tradingRecord/extract")
    @ApiOperation("提现记录")
    public ModelAndView tradingRecordExtract(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/trading-record/extract", "userModel", model);
    }
    /**
     *账单列表
     * @return
     */
    @RequestMapping("/bill")
    @ApiOperation("账单列表")
    public ModelAndView billList(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/bill-list/index", "userModel", model);
    }

    /**
     * 联系客服
     * @return
     */
    @RequestMapping("/contact")
    @ApiOperation("联系客服")
    public ModelAndView contact(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/ucenter/contact", "userModel", model);
    }
    /**
     *用户中心
     * @return
     */
    @RequestMapping("/ucenter/top-up")
    @ApiOperation("用户中心-top")
    public ModelAndView ucenterTopUp(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/top-up/index", "userModel", model);
    }
    /**
     *用户中心
     * @return
     */
    @RequestMapping("/ucenter/bring-up")
    @ApiOperation("用户中心-bring")
    public ModelAndView ucenterBringp(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/bring-up/index", "userModel", model);
    }
    /**
     * 会员管理
     * @return
     */
    @RequestMapping("/vip")
    @ApiOperation("会员管理-bring")
    public ModelAndView vipIndex(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/vip/index", "userModel", model);
    }
    /**
     * 充值页面
     * @return
     */
    @RequestMapping("/ucenter/pay")
    @ApiOperation("充值页面-bring")
    public ModelAndView ucenterPay(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/pay/index", "userModel", model);
    }

    @RequestMapping("/ucenter/intro")
    @ApiOperation("简介页面-bring")
    public ModelAndView ucenterIntro(HttpServletRequest request, Model model) {
        return new ModelAndView("page/pages/ucenter/intro", "userModel", model);
    }
}
