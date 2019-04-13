package com.lontyu.dwjwap.service.impl;

import com.lontyu.dwjwap.constants.TransTypeEnum;
import com.lontyu.dwjwap.dao.*;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.UserInfoVO;
import com.lontyu.dwjwap.dto.req.*;
import com.lontyu.dwjwap.dto.resp.*;
import com.lontyu.dwjwap.entity.*;
import com.lontyu.dwjwap.enums.MoneyRecordEnums;
import com.lontyu.dwjwap.enums.MoneyRecordEnums.MoneyRecordTypeEnum;
import com.lontyu.dwjwap.exception.BizException;
import com.lontyu.dwjwap.service.MoneyRecordService;
import com.lontyu.dwjwap.service.UserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @decription: 用户中心接口
 * @author: as
 * @date: 2018/10/17 0:03
 */
@Service
@Slf4j
public class UserCenterServiceImpl implements UserCenterService {

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private MoneyRecordService moneyRecordService;

    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    @Autowired
    private WechatOrderMapper wechatOrderMapper;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private BjlDrawRecordsMapper bjlDrawRecordsMapper;

    @Override
    public UserCenterInfoRespVO getInfo(Integer userId) {
        if (null == userId) {
            return null;
        }

        log.debug("user center - get info: params[{}]", userId);
        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(userId);
        VipMember vipMember = vipMemberMapper.selectByPrimaryKey(wechatMember.getVipId());

        // 当前用户是否还有未结投注，以及当前余额日志输出
//        List<BjlOrder> bjlOrderList = bjlOrderMapper.selectUserRecordsByResult(userId, 0);
//        log.info("user center - get info: 余额[{}], 未结投注数量[{}]", vipMember.getAmount(), bjlOrderList.size());

        UserCenterInfoRespVO respVO = new UserCenterInfoRespVO();
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(wechatMember.getId());
        userInfoVO.setHeadImg(wechatMember.getHeadImg());
        userInfoVO.setNickName(wechatMember.getNickName());
        userInfoVO.setAmount(Optional.ofNullable(vipMember).map(VipMember::getAmount).orElse(BigDecimal.ZERO));
        userInfoVO.setCommission(Optional.ofNullable(vipMember).map(VipMember::getCommission).orElse(BigDecimal.ZERO));
        userInfoVO.setIsVip(null != vipMember);
        respVO.setUserInfoVO(userInfoVO);

        Date now = new Date();
        if (null != vipMember) {
            // 今日佣金
            respVO.setCommission(moneyRecordService.countByType(vipMember.getId(),
                    MoneyRecordTypeEnum.COMMISSION, now));
            // 今日充值
            respVO.setRecharge(moneyRecordService.countByType(vipMember.getId(),
                    MoneyRecordTypeEnum.RECHARGE, now));
            // 盈亏状况
            BigDecimal bonus = moneyRecordService.countByType(vipMember.getId(),
                    MoneyRecordTypeEnum.BONUS, now);
            BigDecimal stake = moneyRecordService.countByType(vipMember.getId(),
                    MoneyRecordTypeEnum.STAKE, now);
            respVO.setProfitAndLoss(bonus.subtract(stake));
        } else {
            respVO.setCommission(BigDecimal.ZERO);
            respVO.setRecharge(BigDecimal.ZERO);
            respVO.setProfitAndLoss(BigDecimal.ZERO);
        }

        return respVO;
    }

    @Override
    public BaseResponse<List<BetRecordRespVO>> getBetRecords(GetBetRecordsReqVO reqVO) {
        List<BetRecordRespVO> respVOList = new ArrayList<>();
        BaseResponse<List<BetRecordRespVO>> resp = BaseResponse.buildSuccess(respVOList);
        if (null == reqVO) {
            return resp;
        }

        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(reqVO.getUserId());
        if (null == wechatMember.getVipId()) {
            return resp;
        }
        reqVO.setVipId(wechatMember.getVipId());

        log.debug("user center - get bet records: params[{}]", reqVO);
        bjlOrderMapper.selectPageList(reqVO).forEach(bjlOrder -> {
            BetRecordRespVO recordRespVO = new BetRecordRespVO();
            BeanUtils.copyProperties(bjlOrder, recordRespVO);
            SysConfig sysConfig = sysConfigMapper.selectByPrimaryKey(new Long(bjlOrder.getSelectedSize()));
            BigDecimal winMoney = BigDecimal.ZERO;
            if (bjlOrder.getFinalResult() == 1) {
                winMoney = bjlOrder.getBuyAmount().multiply(new BigDecimal(sysConfig.getParamValue()).add(BigDecimal.ONE));
            }
            recordRespVO.setWinMoney(winMoney);

            BjlDrawRecords bjlDrawRecords = bjlDrawRecordsMapper.selectByAwardPeriod(bjlOrder.getPeriods());
            recordRespVO.setDrawResult(bjlDrawRecords.getDrawResult());
            respVOList.add(recordRespVO);
        });
        resp.setTotalCount(bjlOrderMapper.selectPageCount(reqVO));

        return resp;
    }

    @Override
    public BaseResponse<List<ReWiRecordRespVO>> getReWiRecords(GetReWiRecordsReqVO reqVO) {
        List<ReWiRecordRespVO> respVOList = new ArrayList<>();
        BaseResponse<List<ReWiRecordRespVO>> resp = BaseResponse.buildSuccess(respVOList);
        if (null == reqVO) {
            return resp;
        }

        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(reqVO.getUserId());

        GetBillDetailsReqVO getBillDetailsReqVO = new GetBillDetailsReqVO();
        BeanUtils.copyProperties(reqVO, getBillDetailsReqVO);
        getBillDetailsReqVO.setStatus(reqVO.getSign());
        getBillDetailsReqVO.setVipId(wechatMember.getVipId());

        log.debug("user center - get recharge withdraw records: params[{}]", getBillDetailsReqVO);
        moneyRecordMapper.selectPageList(getBillDetailsReqVO).forEach(wechatOrder -> {
            ReWiRecordRespVO recordRespVO = new ReWiRecordRespVO();
            BeanUtils.copyProperties(wechatOrder, recordRespVO);
            recordRespVO.setSign(wechatOrder.getStatus());
            recordRespVO.setOrderid(wechatOrder.getOrderId());
            respVOList.add(recordRespVO);
        });
        resp.setTotalCount(moneyRecordMapper.selectPageCount(getBillDetailsReqVO));

        return resp;
    }

    @Override
    public BaseResponse<List<BillRecordRespVO>> getBillDetails(GetBillDetailsReqVO reqVO) {
        List<BillRecordRespVO> respVOList = new ArrayList<>();
        BaseResponse<List<BillRecordRespVO>> resp = BaseResponse.buildSuccess(respVOList);
        if (null == reqVO) {
            return resp;
        }

        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(reqVO.getUserId());
        if (null == wechatMember.getVipId()) {
            return resp;
        }
        reqVO.setVipId(wechatMember.getVipId());

        log.debug("user center - get bill records: params[{}]", reqVO);
        moneyRecordMapper.selectPageList(reqVO).forEach(moneyRecord -> {
            BillRecordRespVO recordRespVO = new BillRecordRespVO();
            BeanUtils.copyProperties(moneyRecord, recordRespVO);
            respVOList.add(recordRespVO);
        });
        resp.setTotalCount(moneyRecordMapper.selectPageCount(reqVO));

        return resp;
    }

    @Override
    public BaseResponse<MembersRespVO> getMembers(GetMembersReqVO reqVO) {
        MembersRespVO membersRespVO = new MembersRespVO();
        membersRespVO.setCount(0);
        membersRespVO.setCommission(BigDecimal.ZERO);
        List<MemberInfoRespVO> respVOList = new ArrayList<>();
        membersRespVO.setMemberInfoList(respVOList);
        BaseResponse<MembersRespVO> resp = BaseResponse.buildSuccess(membersRespVO);
        if (null == reqVO || reqVO.getLevel() < 1 || reqVO.getLevel() > 5) {
            return resp;
        }

        WechatMember wechatMember = wechatMemberMapper.selectByPrimaryKey(reqVO.getUserId());
        if (null == wechatMember.getVipId()) {
            return resp;
        }
        reqVO.setVipId(wechatMember.getVipId());

        log.debug("user center - get members: params[{}]", reqVO);
        VipMember vipMember = vipMemberMapper.selectByPrimaryKey(reqVO.getVipId());
        List<VipMember> members = Collections.singletonList(vipMember);
        for (int i = 1; i <= reqVO.getLevel(); i++) {
            if (0 == members.size()) {
                break;
            }
            members = vipMemberMapper.selectByInviterIds(members.stream().map(VipMember::getId).collect(Collectors.toList()));
        }

        long[] configIds = {7, 8, 10, 11, 12};
        long configId = configIds[reqVO.getLevel() - 1];
        String rate = sysConfigMapper.selectByPrimaryKey(configId).getParamValue();
        membersRespVO.setRate(new BigDecimal(rate));
        Date now = new Date();
        members.forEach(member -> {
            MemberInfoRespVO memberRespVO = new MemberInfoRespVO();
            WechatMember wm = wechatMemberMapper.selectByVipId(member.getId());
            memberRespVO.setHeadImg(wm.getHeadImg());
            memberRespVO.setNickName(wm.getNickName());
            memberRespVO.setRecharge(moneyRecordService.countByType(member.getId(), MoneyRecordTypeEnum.RECHARGE, now));
            memberRespVO.setConversion(moneyRecordService.countByType(member.getId(), MoneyRecordTypeEnum.WITHDRAW, now));
            memberRespVO.setBeans(member.getAmount());

            // 盈亏状况
            BigDecimal bonus = moneyRecordService.countByType(member.getId(),
                    MoneyRecordTypeEnum.BONUS, now);
            BigDecimal stake = moneyRecordService.countByType(member.getId(),
                    MoneyRecordTypeEnum.STAKE, now);
            memberRespVO.setProfitAndLoss(bonus.subtract(stake));

            // 下注金额
            memberRespVO.setBetAmount(moneyRecordService.countByType(member.getId(), MoneyRecordTypeEnum.STAKE, now));

            // 过去7天下注金额
            List<Map<String, Object>> pastBetAmount = new ArrayList<>();
            LocalDate pastDate = LocalDate.now();
            for (int i = 1; i <= 7; i++) {
                pastDate = pastDate.minusDays(1);
                Map<String, Object> map = new HashMap<>();
                map.put("date", pastDate.format(DateTimeFormatter.ofPattern("MM月dd日")));
                map.put("betAmount", moneyRecordService.countByType(member.getId(), MoneyRecordTypeEnum.STAKE,
                        Date.from(pastDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
                pastBetAmount.add(map);
            }
            memberRespVO.setPastBetAmount(pastBetAmount);

            BigDecimal commission = stake.multiply(new BigDecimal(rate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
            membersRespVO.setCommission(membersRespVO.getCommission().add(commission));

            respVOList.add(memberRespVO);
        });

        membersRespVO.setCount(members.size());

        return resp;
    }

    @Override
    public void applyWithdraw(ApplyWithdrawReqVO reqVO) {
        if (null == reqVO || null == reqVO.getUserId()) {
            throw new BizException("系统异常");
        }

        BigDecimal amount = Optional.ofNullable(reqVO.getAmount()).orElse(BigDecimal.ZERO);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("提现金额无效");
        }

        VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(reqVO.getUserId());
        if (null == vipMember) {
            throw new BizException("用户不存在");
        }
        if (StringUtils.isBlank(vipMember.getMobile())) {
            throw new BizException("未绑定手机号，不能进行该操作");
        }
        if (vipMember.getStatus() == 1) {
            throw new BizException("已冻结用户，不能进行该操作");
        }
        if (amount.compareTo(vipMember.getAmount()) > 0) {
            throw new BizException("余额不足");
        }

        Date now = new Date();
        // 插入提现申请记录
        WechatOrder wechatOrder = new WechatOrder();
        wechatOrder.setUserId(vipMember.getId());
        wechatOrder.setAmount(amount);
        wechatOrder.setCreateTime(now);
        wechatOrder.setType(2);
        wechatOrderMapper.insertSelective(wechatOrder);

        // 插入流水
        MoneyRecord moneyRecord = new MoneyRecord();
        moneyRecord.setVipId(vipMember.getId());
        moneyRecord.setAmount(amount);
        moneyRecord.setRemark("用户申请提现");
        moneyRecord.setStatus(MoneyRecordEnums.MoenyRecordStatus.SUCCESS.getCode());
        moneyRecord.setType(TransTypeEnum.WITHDRAW_APPLY.getType());
        moneyRecord.setCreateTime(now);
        moneyRecord.setOrderId(wechatOrder.getId() + "");
        moneyRecordMapper.insert(moneyRecord);

        // 减去用户余额
        Map<String, Object> params = new HashMap<>();
        params.put("id", vipMember.getId());
        params.put("version", vipMember.getVersion());
        params.put("amount", amount.multiply(new BigDecimal("-1")));
        vipMemberMapper.updateAmountByVesion(params);
    }
}
