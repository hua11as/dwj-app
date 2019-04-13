package com.lontyu.dwjwap.controller;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.constants.PrizeResultEnum;
import com.lontyu.dwjwap.dao.WechatMemberMapper;
import com.lontyu.dwjwap.dto.*;
import com.lontyu.dwjwap.dto.req.CurrentInfoReq;
import com.lontyu.dwjwap.dto.req.PrizeResultReq;
import com.lontyu.dwjwap.dto.resp.TrendChartDataRespVO;
import com.lontyu.dwjwap.entity.SysConfig;
import com.lontyu.dwjwap.entity.WechatMember;
import com.lontyu.dwjwap.service.BjlService;
import com.lontyu.dwjwap.service.SysConfigService;
import com.lontyu.dwjwap.socket.MessageEventHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 功能说明: 百家乐 控制器
 */
@Controller
@RequestMapping("/bjl")
@Api(value = "BjlController", tags = "百家乐控制器")
public class BjlController {

    private static Logger logger = LoggerFactory.getLogger(BjlController.class);

    @Autowired
    private BjlService bjlService;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/{userId}")
    @ApiOperation("首页")
    public String index(@PathVariable("userId") Integer userId,Model model){
        WechatMember member = wechatMemberMapper.selectByPrimaryKey(userId);
        if(member!=null) {
            model.addAttribute("userId", member.getId());
            model.addAttribute("headImg", member.getHeadImg());
        }else{
            logger.error("用户ID错误， userId:"+userId);
        }
        return "page/pages/baijiale/baijiale";
    }

    /**
     * 用户下注接口
     *
     * @param vo
     * @return
     */
    @ApiOperation(value="提交订单", notes="提交订单")
    @PostMapping(value = "/submitOrder")
    @ResponseBody
    public BaseResponse bjlSubmitOrder(@RequestBody SubmitOrderVo vo) {
        logger.info("[百家乐下单]-接收到前端页面发来的请求，请求参数：bjlOrderVo=" + JSONObject.toJSONString(vo));
        BaseResponse result = new BaseResponse();
        try {
            result = bjlService.submitOrder( getBjlOrderVo(vo));
        }catch (Exception e){
            result.setCode(BaseResponse.FAIL_CODE);
            result.setMsg(e.getMessage());
        }
        logger.info("[百家乐下单]-响应结果回前端页面，请求参数：bjlOrderVo=" + JSONObject.toJSONString(vo) + "响应结果：result=" + result.toString());
        return result;
    }


    /**
     * 根据前端下注转换成下注订单
     * @param vo
     * @return
     */
    private  List<BjlOrderVo> getBjlOrderVo(SubmitOrderVo vo){
        List<BjlOrderVo> list = new ArrayList<>();
         for(int i=0;i<5;i++){
             if(vo.getBuyAmount().get(i).intValue()>0) {
                 BjlOrderVo ord = new BjlOrderVo();
                 ord.setUserId(vo.getUserId());
                 //(1、闲 ；2、闲对；3 、和；4、庄对；5、庄)"

                 ord.setSelectedSize(i+1);
                 ord.setPeriods(vo.getPeriods());
                 ord.setBuyAmount(vo.getBuyAmount().get(i));
                 ord.setProductSerial("DaWanJia");
                 //押注哪方赢  （1 闲胜 2庄胜，3 和 ）
                 if(i==0||i==1) {
                     ord.setSupportWin(PrizeResultEnum.XWin.getCode());
                 }else if(i==2){
                     ord.setSupportWin(PrizeResultEnum.HE.getCode());
                 }else{
                     ord.setSupportWin(PrizeResultEnum.ZWin.getCode());
                 }
                 list.add(ord);
             }
         }
        return list;
    }



    /**
     * 检验 是否需要进行视频切换
     *
     * @return
     */
    @GetMapping(value = "/checkIsSwitch")
    @ResponseBody
    public JSONObject checkIsSwitch(@RequestParam("productSerial") String productSerial, @RequestParam("currentDate") String currentDate,
                                    @RequestParam("currentPeriods") Integer currentPeriods) {
        logger.info("[百家乐开奖结果]-接收到前端页面发来的请求，请求参数：productSerial=" + productSerial +",currentDate="+currentDate +",currentPeriods="+currentPeriods);
        JSONObject result = bjlService.isSwitchVideo(productSerial,currentDate,currentPeriods);
        logger.info("[百家乐开奖结果]-接收到前端页面发来的请求，请求参数：productSerial=" + productSerial +",currentDate="+currentDate +",currentPeriods="+currentPeriods + "，返回结果：" + result.toString());
        return result;
    }


    /**
     * 查询当前期信息接口
     * @return
     */
    @RequestMapping(value = "/currentInfo",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取系统当前信息", notes = "获取系统当前信息，返回当前视频播放相关信息")
    public BaseResponse<CurrentInfoVo> getCurrentInfo() {
        logger.info("[百家乐查询当前系统信息]-"+ new Date());
        BaseResponse<CurrentInfoVo> result = bjlService.currentInfo();
        logger.info("[百家乐查询当前系统信息]-响应结果回前端页面 响应结果：result=" + JSONObject.toJSONString(result));
        return result;
    }


    /**
     * 查询当前期开奖结果
     * @return
     */
    @RequestMapping(value = "/prizeResult",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询当前期开奖结果", notes = "根据期数查询当期用户下注结果")
    public BaseResponse<PrizeResultVo> getPrizeResult(PrizeResultReq req) {
        logger.info("[查询当前期开奖结果]-"+ new Date());
        BaseResponse<PrizeResultVo> result = bjlService.prizeResult(req);
        logger.info("[查询当前期开奖结果]-响应结果回前端页面 响应结果：result=" + JSONObject.toJSONString(result));
        return result;
    }

    @RequestMapping(value = "/getTrendChartData")
    @ResponseBody
    @ApiOperation("获取近走势图开奖记录，期数倒序")
    public BaseResponse<List<TrendChartDataRespVO>> getTrendChartData() {
        return bjlService.getTrendChartData();
    }

    @RequestMapping(value = "/getSystemNotice")
    @ResponseBody
    @ApiOperation("获取系统公告")
    public BaseResponse<String> getSystemNotice() {
        String notice = Optional.ofNullable(sysConfigService.getSysConfigByParamKey("systemNotice")).
                map(SysConfig::getParamValue).orElse("");
        return BaseResponse.buildSuccess(notice);
    }
}
