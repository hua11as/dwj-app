package com.lontyu.dwjwap.service;

import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.req.*;
import com.lontyu.dwjwap.dto.resp.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @decription: 用户中心接口
 * @author: as
 * @date: 2018/10/17 0:05
 */
public interface UserCenterService {

    /**
     * 获取用户中心信息
     *
     * @param userId 用户id
     * @return 用户中心信息
     */
    UserCenterInfoRespVO getInfo(Integer userId);

    /**
     * 获取投注记录
     *
     * @param reqVO request VO
     * @return 投注记录
     */
    BaseResponse<List<BetRecordRespVO>> getBetRecords(GetBetRecordsReqVO reqVO);

    /**
     * 获取充值提现记录
     *
     * @param reqVO request VO
     * @return 充值提现记录
     */
    BaseResponse<List<ReWiRecordRespVO>> getReWiRecords(GetReWiRecordsReqVO reqVO);

    /**
     * 获取流水记录
     *
     * @param reqVO request VO
     * @return 获取流水记录
     */
    BaseResponse<List<BillRecordRespVO>> getBillDetails(GetBillDetailsReqVO reqVO);

    /**
     * 获取会员信息
     *
     * @param reqVO request VO
     * @return 会员信息
     */
    BaseResponse<MembersRespVO> getMembers(GetMembersReqVO reqVO);

    /**
     * 申请提现
     *
     * @param reqVO request VO
     */
    void applyWithdraw(ApplyWithdrawReqVO reqVO);
}
