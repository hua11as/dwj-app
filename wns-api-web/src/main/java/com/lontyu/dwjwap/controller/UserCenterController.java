package com.lontyu.dwjwap.controller;

import com.lontyu.dwjwap.config.SystemContext;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.req.*;
import com.lontyu.dwjwap.dto.resp.*;
import com.lontyu.dwjwap.entity.SysConfig;
import com.lontyu.dwjwap.service.RechargeQrcodeService;
import com.lontyu.dwjwap.service.SysConfigService;
import com.lontyu.dwjwap.service.UserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @description: TODO[as] 用户中心Controller
 * @author: as
 * @date: 2018/10/13 23:15
 */
@Controller
@RequestMapping("/userCenter")
@Api(value = "UserCenterController", tags = "用户中心API")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private RechargeQrcodeService rechargeQrcodeService;

    /**
     * 获取用户信息
     *
     * @return 返回用户信息
     */
    @GetMapping("/getUserInfo")
    @ApiOperation("获取用户信息")
    @ResponseBody
    public BaseResponse<UserCenterInfoRespVO> getUserInfo() {
        Integer userId = SystemContext.getCurrentUser();
        return BaseResponse.buildSuccess(userCenterService.getInfo(userId));
    }

    /**
     * 获取投注记录
     * <p>
     * 参数: 分页信息、日期条件
     *
     * @return 投注记录
     */
    @PostMapping("getBetRecords")
    @ApiOperation("获取投注信息")
    @ResponseBody
    public BaseResponse<List<BetRecordRespVO>> getBetRecords(@RequestBody GetBetRecordsReqVO reqVO) {
        reqVO.setUserId(SystemContext.getCurrentUser());
        return userCenterService.getBetRecords(reqVO);
    }

    /**
     * 获取充值提现记录
     * <p>
     * 参数: 分页信息、日期条件
     *
     * @return 充值提现记录
     */
    @PostMapping("getReWiRecords")
    @ApiOperation("获取充值提现记录")
    @ResponseBody
    public BaseResponse<List<ReWiRecordRespVO>> getReWiRecords(@RequestBody GetReWiRecordsReqVO reqVO) {
        reqVO.setUserId(SystemContext.getCurrentUser());
        return userCenterService.getReWiRecords(reqVO);
    }

    /**
     * 获取账单明细
     * <p>
     * 参数: 分页信息、日期条件、收支、账单类目
     *
     * @return 账单明细
     */
    @PostMapping("getBillDetails")
    @ApiOperation("获取账单明细")
    @ResponseBody
    public BaseResponse<List<BillRecordRespVO>> getBillDetails(@RequestBody GetBillDetailsReqVO reqVO) {
        reqVO.setUserId(SystemContext.getCurrentUser());
        return userCenterService.getBillDetails(reqVO);
    }

    @PostMapping("getMemebers")
    @ApiOperation("获取会员列表")
    @ResponseBody
    public BaseResponse<MembersRespVO> getMembers(@RequestBody GetMembersReqVO reqVO) {
        reqVO.setUserId(SystemContext.getCurrentUser());
        return userCenterService.getMembers(reqVO);
    }

    @PostMapping("applyWithdraw")
    @ApiOperation("申请提现")
    @ResponseBody
    public BaseResponse applyWithdraw(@RequestBody ApplyWithdrawReqVO reqVO) {
        reqVO.setUserId(SystemContext.getCurrentUser());
        userCenterService.applyWithdraw(reqVO);
        return BaseResponse.buildSuccess(null);
    }

    @PostMapping("getRechargeAmount")
    @ApiOperation("获取充值金额")
    @ResponseBody
    public BaseResponse<List<String>> getRechargeAmount() {
        final String rechargeAmountConfigKey = "rechargeAmountConfig";
        String rechargeAmountConfig = sysConfigService.getSysConfigByParamKey(rechargeAmountConfigKey).getParamValue();
        List<String> rechargeAmountConfigList = Arrays.asList(rechargeAmountConfig.split(","));
        return BaseResponse.buildSuccess(rechargeAmountConfigList);
    }

    @PostMapping("getRechargeQrcode")
    @ApiOperation("获取充值二维码")
    @ResponseBody
    public BaseResponse<RechargeQrcodeRespVO> getRechargeQrcode(@RequestBody GetRechargeQrcodeReqVO rechargeQrcodeReqVO) {
        rechargeQrcodeReqVO.setUserId(SystemContext.getCurrentUser());
        return BaseResponse.buildSuccess(rechargeQrcodeService.getRechargeQrcode(rechargeQrcodeReqVO));
    }
}
