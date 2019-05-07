package com.lontyu.dwjwap.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.constants.PrizeResultEnum;
import com.lontyu.dwjwap.constants.SelectedSizeEnum;
import com.lontyu.dwjwap.config.SystemConstant;
import com.lontyu.dwjwap.constants.TransTypeEnum;
import com.lontyu.dwjwap.dao.*;
import com.lontyu.dwjwap.dto.*;
import com.lontyu.dwjwap.dto.req.PrizeResultReq;
import com.lontyu.dwjwap.dto.resp.TrendChartDataRespVO;
import com.lontyu.dwjwap.entity.*;
import com.lontyu.dwjwap.entity.MoneyRecord;
import com.lontyu.dwjwap.entity.VipMember;
import com.lontyu.dwjwap.enums.MoneyRecordEnums;
import com.lontyu.dwjwap.enums.SysConfigEnum;
import com.lontyu.dwjwap.service.BjlService;
import com.lontyu.dwjwap.utils.DateUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.lontyu.dwjwap.constants.SelectedSizeEnum.getSelectedSizeEnum;

/**
 * 百家乐相关 Service
 */
@Service("bjlService")
public class BjlServiceImpl  implements BjlService{

    private static Logger log = LoggerFactory.getLogger(BjlServiceImpl.class);

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private MoneyRecordMapper moneyRecordMapper;

    @Autowired
    private BjlEndChipinMapper endChipinMapper;

    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    @Autowired
    private BjlOpenprizeVideoMapper openprizeVideoMapper;

    @Autowired
    private BjlDrawRecordsMapper drawRecordsMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private BjlDrawRecordsMapper bjlDrawRecordsMapper;

//    @Autowired
//    RedisUtil redisUtil;

    /**
     * 下注订单提交
     *
     * @param bjlOrderVos
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse submitOrder(List<BjlOrderVo> bjlOrderVos) throws Exception{
        try {

            BaseResponse response = new BaseResponse();
            response.setCode(BaseResponse.SUCCESS_CODE);
            response.setMsg("投注成功！");

            if (bjlOrderVos.isEmpty()) {
                response.setCode(BaseResponse.FAIL_CODE);
                response.setMsg("投注内容不能为空！");
                return response;
            }

            //1、验证会员
            WechatMember weChatMember = wechatMemberMapper.selectByPrimaryKey(bjlOrderVos.get(0).getUserId());
            VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(weChatMember.getVipId());
            if (vipMember == null) {
                response.setCode(BaseResponse.FAIL_CODE);
                response.setMsg("你还不是会员，请先绑定会员！");
                return response;
            }
            for (BjlOrderVo bjlOrderVo : bjlOrderVos) {
                //2、 校验下注金额
                BigDecimal buyAmount = bjlOrderVo.getBuyAmount();
                if (buyAmount.intValue() <= 0) {
//                    response.setCode(BaseResponse.FAIL_CODE);
//                    response.setMsg("押注金额不能小于0！");
//                    return response;
                    throw  new Exception("押注哪一方胜出不能为空！");
                }

                // 3、校验押注方、押注大小是否为空
                if (bjlOrderVo.getSupportWin() == null || bjlOrderVo.getSelectedSize() == null) {
//                    response.setCode(BaseResponse.FAIL_CODE);
//                    response.setMsg("押注哪一方胜出不能为空！");
//                    return response;
                    throw  new Exception("押注哪一方胜出不能为空！");
                }

                //4、校验下注时间是否截止
                BjlDrawRecords records = bjlDrawRecordsMapper.selectByAwardPeriod(bjlOrderVo.getPeriods());
                if (records.getEndOrderTime().compareTo(new Date()) < 0) {
//                    response.setCode(BaseResponse.FAIL_CODE);
//                    response.setMsg("下注时间已经终止！");
//                    return response;
                    throw  new Exception("下注时间已经终止！");
                }
                //5、验证余额
                if (vipMember.getAmount().compareTo(buyAmount) < 0) {
//                    response.setCode(BaseResponse.FAIL_CODE);
//                    response.setMsg("余额不足！");
//                    return response;
                    throw  new Exception("您的金豆不足，请先进行充值！");
                }
                log.info("[百家乐下单]-用户：userid=" + bjlOrderVo.getUserId() + ",开始扣款...");

                // 6、扣除账户余额款
                vipMember.setAmount(vipMember.getAmount().subtract(buyAmount));
                vipMemberMapper.updateByPrimaryKey(vipMember);

                // 7、记录下注明细表
                BjlOrder bjlOrder = new BjlOrder();
                bjlOrder.setVipId(vipMember.getId());
                bjlOrder.setPeriods(bjlOrderVo.getPeriods());
                bjlOrder.setBuyAmount(bjlOrderVo.getBuyAmount());
                bjlOrder.setSupportWin(bjlOrderVo.getSupportWin());
                bjlOrder.setSelectedSize(bjlOrderVo.getSelectedSize());
                bjlOrderMapper.insert(bjlOrder);

                //8、记录用户资金流水表
                MoneyRecord moneyRecord = new MoneyRecord();
                moneyRecord.setVipId(vipMember.getId());
                moneyRecord.setAmount(buyAmount);
                moneyRecord.setRemark("下注百家乐");
                moneyRecord.setStatus(MoneyRecordEnums.MoenyRecordStatus.SUCCESS.getCode());
                moneyRecord.setType(TransTypeEnum.CHIP_IN.getType());
                moneyRecord.setCreateTime(new Date());
                moneyRecord.setOrderId(bjlOrder.getId() + "");
                moneyRecordMapper.insert(moneyRecord);

                //9、平台账户收入
                VipMember platForm = vipMemberMapper.selectByPrimaryKeyForUpdate(SystemConstant.PLATFORM_VIP_ID);
                platForm.setAmount(platForm.getAmount().add(buyAmount));
                vipMemberMapper.updateByPrimaryKey(platForm);

                //10、记录平台资金流水记录
                MoneyRecord PlatFormMoneyRecord = new MoneyRecord();
                PlatFormMoneyRecord.setVipId(platForm.getId());
                PlatFormMoneyRecord.setAmount(buyAmount);
                PlatFormMoneyRecord.setStatus(MoneyRecordEnums.MoenyRecordStatus.SUCCESS.getCode());
                PlatFormMoneyRecord.setRemark("下注百家乐平台收入");
                PlatFormMoneyRecord.setType(TransTypeEnum.PLATFORM_CHIP_IN.getType());
                PlatFormMoneyRecord.setOrderId(bjlOrder.getId() + "");
                PlatFormMoneyRecord.setCreateTime(new Date());
                moneyRecordMapper.insert(PlatFormMoneyRecord);

                log.info("[百家乐下单]-用户：userid=" + bjlOrderVo.getUserId() + ",完成扣款...");
            }

            response.setCode(BaseResponse.SUCCESS_CODE);
            response.setMsg("下注成功！");
            return response;
        }catch (Exception e){
            log.error("投注异常",e);
            throw  e;
        }
    }


    /**
     * 检验是否切换视频地址
     *
     * @param productSerial
     * @param currentDate
     * @param currentPeriods
     * @return
     */
    @Transactional
    public JSONObject isSwitchVideo(String productSerial, String currentDate, Integer currentPeriods) {
        JSONObject result = new JSONObject();

        // 1、校验参数
        if (StringUtils.isBlank(productSerial) || StringUtils.isBlank(currentDate)
                || currentPeriods == null) {
            result.put("code", 1);
            result.put("msg", "请求参数有误！");
            return result;
        }

        // 2、计算当前这期 庄闲 卖家下注总改赔偿总金额
        List<SysConfig> sysConfigs = null;//sysConfigMapper.selectNeedRecords();
        if (Objects.isNull(sysConfigs) || sysConfigs.size() != 6) {
            result.put("code", 1);
            result.put("msg", "参数配置有误!");
            return result;
        }

        double XOdds = Double.parseDouble(sysConfigs.get(0).getParamValue()); // 闲 1
        double XDOdds = Double.parseDouble(sysConfigs.get(1).getParamValue()); // 闲对 2
        double HOdds = Double.parseDouble(sysConfigs.get(2).getParamValue()); // 和 3
        double ZDOdds = Double.parseDouble(sysConfigs.get(3).getParamValue()); // 庄对 4
        double ZOdds = Double.parseDouble(sysConfigs.get(4).getParamValue()); // 庄 5
        double allowRange = Double.parseDouble(sysConfigs.get(5).getParamValue()); // 允许误差值 6

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("supportWin", 1); // 庄胜
        paramsMap.put("currentDate", currentDate);
        List<BjlOrder> zWinLists = bjlOrderMapper.getAmountByDate(paramsMap);

        BigDecimal zWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : zWinLists) {
            int selectedSize = order.getSelectedSize().intValue();
            BigDecimal buyAmount = order.getBuyAmount();
            zWinAmount = getTotalAmount(XOdds, XDOdds, HOdds, ZDOdds, ZOdds,
                    zWinAmount, selectedSize, buyAmount);
        }

        paramsMap.put("supportWin", 0); // 闲胜
        List<BjlOrder> xWinLists = bjlOrderMapper.getAmountByDate(paramsMap);
        BigDecimal xWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : xWinLists) {
            int selectedSize = order.getSelectedSize().intValue();
            BigDecimal buyAmount = order.getBuyAmount();
            xWinAmount = getTotalAmount(XOdds, XDOdds, HOdds, ZDOdds, ZOdds,
                    xWinAmount, selectedSize, buyAmount);
        }

        boolean zWin = false;
        BigDecimal midResult = zWinAmount.subtract(xWinAmount);
        if (midResult.intValue() > 0) {
            zWin = true;
        }

        BigDecimal computePriceDiff = midResult.abs(); // 双方下注总金额差价

        // 3、获取当前这期原本播放的是胜负
        BjlEndChipin endChipIn = new BjlEndChipin();
        endChipIn.setVideoSerial(productSerial);
        endChipIn.setCurrentPeriods(currentPeriods);
        endChipIn = endChipinMapper.selectAllByObject(endChipIn);
        if (Objects.isNull(endChipIn)) {
            result.put("code", 1);
            result.put("msg", "系统错误！");
            return result;
        }

        Integer systemWin = endChipIn.getCurrentResult();

        // 4、判断是否需要切换
        if (computePriceDiff.compareTo(new BigDecimal(allowRange)) < 0) { // 如果差价在我们允许范围内，那么不用切换视频地址
            result.put("code", 0);
            result.put("msg", "处理成功！");
            result.put("isWin", systemWin); //（0、庄胜；1、闲胜）
            result.put("url", ""); // url 地址留空
            result.put("zjPoints",endChipIn.getBankerPoint()); // 庄家牌
            result.put("xjPoints",endChipIn.getPlayerPoint()); // 闲家牌
        } else {
            int sign = 0;
            // 改变 预期开奖结果
            if (zWin) {
                sign = 1;
            }
            paramsMap.put("resultSign", sign);
            List<BjlOpenprizeVideo> openprizeVideos = null;// openprizeVideoMapper.listBy(paramsMap);

            int i = new Random().nextInt(openprizeVideos.size()); //获取随机视频
            BjlOpenprizeVideo openprizeVideo = openprizeVideos.get(i);

            result.put("code", 0);
            result.put("msg", "处理成功！");
            result.put("isWin", zWin ? 0 : 1); //（0、庄胜；1、闲胜）
            result.put("url", openprizeVideo.getLinkAdress()); // url 地址留空
            result.put("zjPoints",openprizeVideo.getBankerPoint()); // 庄家牌
            result.put("xjPoints",openprizeVideo.getPlayerPoint()); // 闲家牌
        }

        // 5、登记这期的开奖记录
        BjlDrawRecords drawRecords = new BjlDrawRecords();
        drawRecords.setAwardPeriod(DateUtils.getReqDateyyyyMMdd(new Date()) + String.format("%04d", currentPeriods));
        drawRecords.setDrawResult(zWin ? 1 : 2);//（1、庄胜；2、闲胜）
        drawRecords.setStatisticalMethod(0);
        drawRecordsMapper.insert(drawRecords);

        return result;
    }

    /**
     *   计算总金额
     * @param XOdds
     * @param XDOdds
     * @param HOdds
     * @param ZDOdds
     * @param ZOdds
     * @param zWinAmount
     * @param selectedSize
     * @param buyAmount
     * @return
     */
    private BigDecimal getTotalAmount(double XOdds, double XDOdds, double HOdds,
                                      double ZDOdds, double ZOdds, BigDecimal zWinAmount,
                                      int selectedSize, BigDecimal buyAmount) {
        if (selectedSize == SelectedSizeEnum.XIAN.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(XOdds)));
        } else if (selectedSize == SelectedSizeEnum.XIANDUI.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(XDOdds)));
        } else if (selectedSize == SelectedSizeEnum.HE.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(HOdds)));
        } else if (selectedSize == SelectedSizeEnum.ZHUANGDUI.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(ZDOdds)));
        } else {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(ZOdds)));
        }
        return zWinAmount;
    }


    /**
     *获取系统当前信息
     *
     * @return
     */
    public BaseResponse<CurrentInfoVo> currentInfo() {

        BaseResponse resp = new BaseResponse();
        resp.setCode(BaseResponse.SUCCESS_CODE);

        CurrentInfoVo vo = new CurrentInfoVo();
        vo.setSysCurrentTime(new Date().getTime());
        resp.setData(vo);

        //检查当前系统是否在营业期间
        SysConfig  openStatusConfig = sysConfigMapper.selectByParamKey(SysConfigEnum.SYS_STATUS.getKey());
        System.out.println("sysconfig: " + JSONObject.toJSONString(openStatusConfig));
        if(openStatusConfig==null || openStatusConfig.getParamValue()==null || !openStatusConfig.getParamValue().equals("1")){
            log.info("当前系统未配置营业状态或处理停业状态： openStatusConfig:"+openStatusConfig==null? "": JSONObject.toJSONString(openStatusConfig));
            resp.setCode(BaseResponse.FAIL_CODE);
            vo.setOpenStatus(0);
            resp.setMsg("当前时间未营业");

            return resp;
        }

        //设置当前营业状态为正常营业状态
        vo.setOpenStatus(1);

        //获取系统赔率配置
        vo.setOdds(getOddsVo());

       //检查当前是不否有在进行中的记录，无记录只等待3秒再查一次，如再无则返回异常
         BjlDrawRecords record = drawRecordsMapper.selectActiveRecord();
        if(record==null){
            try {
                log.info("当前为营业中，未生成当前进行中记录，休眠3秒");
                Thread.sleep(3000);
            }catch (Exception e){
                log.error("获取当前进行中记录休眠异常",e);
            }
        }

        record = drawRecordsMapper.selectActiveRecord();
        if(record==null){
            log.error("获取当前进行中记录异常，请稍后再试");
            resp.setCode(BaseResponse.FAIL_CODE);
            resp.setMsg("获取当前期信息异常，请稍后再试");
            return resp;
        }

        vo.setCurrentPeriod(record.getAwardPeriod());
        //下注期间返回下注相关信息,返回下注时间与预计播放的视频
        Date currentDate = new Date();
        if(currentDate.compareTo(record.getStartOrderTime()) >= 0 && currentDate.compareTo(record.getEndOrderTime())<=0){
            vo.setEndOrderTime(record.getEndOrderTime().getTime());
            vo.setStartOrderTime(record.getStartOrderTime().getTime());
            vo.setPreVideo1Id(record.getPreVideo1());
            vo.setPreVideo1URL(openprizeVideoMapper.selectByPrimaryKey(record.getPreVideo1()).getLinkAdress());
            vo.setPreVideo2Id(record.getPreVideo2());
            vo.setPreVideo2URL(openprizeVideoMapper.selectByPrimaryKey(record.getPreVideo2()).getLinkAdress());
            vo.setCurrentStatus(CurrentInfoVo.CurrentStatus.ORDER_TIME.getCode());
            vo.setCurrentStatusDesc(CurrentInfoVo.CurrentStatus.ORDER_TIME.getDesc());
            return resp;
        }

        //视频播放中，返回播放信息
        if(currentDate.compareTo(record.getStartWaitPlayTime())>= 0 && currentDate.compareTo(record.getEndWaitPlayTime())<=0){
            vo.setStartWaitPlayTime(record.getStartWaitPlayTime().getTime());
            vo.setEndWaitPlayTime(record.getEndWaitPlayTime().getTime());
            vo.setCurrentStatus(CurrentInfoVo.CurrentStatus.WAIT_PLAY_TIME.getCode());
            vo.setCurrentStatusDesc(CurrentInfoVo.CurrentStatus.WAIT_PLAY_TIME.getDesc());
            return resp;
        }

        //视频播放中，返回播放信息
        if(null != record.getStartPlayTime() && null != record.getEndPlayTime() &&
                currentDate.compareTo(record.getStartPlayTime())>= 0 && currentDate.compareTo(record.getEndPlayTime())<=0){
            vo.setStartPlayTime(record.getStartPlayTime().getTime());
            vo.setEndPlayTime(record.getEndPlayTime().getTime());
            vo.setPlayVideoId(record.getAwardVideo());
            vo.setPlayVideo1URL(openprizeVideoMapper.selectByPrimaryKey(record.getAwardVideo()).getLinkAdress());
            vo.setCurrentStatus(CurrentInfoVo.CurrentStatus.PLAY_TIME.getCode());
            vo.setCurrentStatusDesc(CurrentInfoVo.CurrentStatus.PLAY_TIME.getDesc());
            return resp;
        }

        //开奖结果展示，返回开奖信息
        if(null != record.getStartShowResultTime() && null != record.getEndShowResultTime() &&
                currentDate.compareTo(record.getStartShowResultTime())>=0 && currentDate.compareTo(record.getEndShowResultTime())<=0){
            BjlOpenprizeVideo v = openprizeVideoMapper.selectByPrimaryKey(record.getAwardVideo());
            vo.setStartShowResultTime(record.getStartShowResultTime().getTime());
            vo.setEndShowResultTime(record.getEndShowResultTime().getTime());
            vo.setCurrentPeroidResult(record.getDrawResult());
            vo.setCurrentStatus(CurrentInfoVo.CurrentStatus.SHOW_TIME.getCode());
            vo.setCurrentStatusDesc(CurrentInfoVo.CurrentStatus.SHOW_TIME.getDesc());
            //庄，闲，开牌结果
            vo.setBankResult(v.getBankerPoint());
            vo.setBankResultColor(v.getBankerPointColor());
            vo.setClientResult(v.getPlayerPoint());
            vo.setClientResultColor(v.getPlayerPointColor());
            vo.setPlayerPair(v.getPlayerPair());
            vo.setBankerPair(v.getBankerPair());
            return resp;
        }

        log.error("当前期数据异常，currentime:"+currentDate.toString() +"  record:"+JSONObject.toJSONString(record));
        resp.setCode(-2);
        resp.setMsg("请稍后重试");
        return resp;

    }

    @Override
    public BaseResponse<PrizeResultVo> prizeResult(PrizeResultReq req) {
        BaseResponse resp = new BaseResponse();
        resp.setCode(BaseResponse.SUCCESS_CODE);
        //1、验证会员
        WechatMember weChatMember = wechatMemberMapper.selectByPrimaryKey(req.getUserId());
        VipMember vipMember = vipMemberMapper.selectByPrimaryKeyForUpdate(weChatMember.getVipId());
        if (vipMember == null) {
            resp.setCode(BaseResponse.FAIL_CODE);
            resp.setMsg("你还不是会员，请先绑定会员！");
            return resp;
        }

        BjlDrawRecords records =  drawRecordsMapper.selectByAwardPeriod(req.getPeroid());
        if(records==null){
            resp.setCode(BaseResponse.FAIL_CODE);
            resp.setMsg("开奖期数错误！");
            return resp;
        }
        /**
         * 判断是否已开奖
         */
        if(records.getStartShowResultTime()==null || new Date().compareTo(records.getStartShowResultTime())<0){
            resp.setCode(BaseResponse.SUCCESS_CODE);
            resp.setMsg("本期未开奖！");
            return resp;
        }

        PrizeResultVo vo = new PrizeResultVo();
        vo.setAccountAmount(vipMember.getAmount());
        vo.setPeroid(req.getPeroid());
        vo.setResult(PrizeResultVo.RESULT_LOST);

         List<BjlOrder> ods= bjlOrderMapper.getOrderByUserIdAndPeroid(vipMember.getId(),req.getPeroid());
        if(ods==null || ods.isEmpty()){
            resp.setCode(BaseResponse.SUCCESS_CODE);
            resp.setMsg("本期！投注记录为空");
            return resp;
        }

        Odds odds = this.getOddsConfig();
        BigDecimal earnAmount= BigDecimal.ZERO;
        BjlOpenprizeVideo video = openprizeVideoMapper.selectByPrimaryKey(records.getAwardVideo());
        for(BjlOrder od:ods){
            earnAmount = earnAmount.subtract(od.getBuyAmount());
            if(userPrizeResult(od.getSelectedSize(),video)){
                 //返还下注金额 *（1+赔率）
                 earnAmount= earnAmount.add(od.getBuyAmount().multiply(
                                 odds.getOddsValue(getSelectedSizeEnum(od.getSelectedSize())).add(BigDecimal.ONE)));
             }
        }
      vo.setPeroidAmount(earnAmount);
        //设置结果
        if(earnAmount.intValue()>0){
            vo.setResult(PrizeResultVo.RESULT_WIN);
        }
        resp.setMsg("查询结果成功");
        resp.setData(vo);
        return resp;
    }

    /**
     * 判断结果是否中奖
     * @param selectedSize 用户下注结果
     * @param video 开奖视频
     * @return
     */
    private boolean  userPrizeResult(int selectedSize, BjlOpenprizeVideo video){
        SelectedSizeEnum sysParamEnum = getSelectedSizeEnum(selectedSize);
        if (null == sysParamEnum) {
            return false;
        }
        boolean rs = false;
        switch (sysParamEnum){
            case XIAN:
                if (video.getResultSign() == PrizeResultEnum.XWin.getCode()) {
                    rs = true;
                }
                break;
            case XIANDUI:
                if (1 == video.getPlayerPair()) {
                    rs = true;
                }
                break;
            case HE:
                if (video.getResultSign() == PrizeResultEnum.HE.getCode()) {
                    rs = true;
                }
                break;
            case ZHUANGDUI:
                if (1 == video.getBankerPair()) {
                    rs = true;
                }
                break;
            case ZHUANG:
                if (video.getResultSign() == PrizeResultEnum.ZWin.getCode()) {
                    rs = true;
                }
                break;
            default:
                break;
        }
        return rs;
    }

    @Override
    public Odds getOddsConfig() {

//         Object savedOdds = redisUtil.get(RedisKeysEnum.SYSTEM_ODDS_CONFIG.getKey());
//        if(savedOdds != null){
//            log.info("从缓存中读取配置： odds:"+JSONObject.toJSONString(savedOdds));
//           return (Odds)savedOdds;
//        }
        Odds odds = new Odds();
        BigDecimal XOdds = new BigDecimal(sysConfigMapper.selectByPrimaryKey(1l).getParamValue()); // 闲 1
        BigDecimal XDOdds = new BigDecimal(sysConfigMapper.selectByPrimaryKey(2l).getParamValue()); // 闲对 2
        BigDecimal HOdds = new BigDecimal(sysConfigMapper.selectByPrimaryKey(3l).getParamValue()); // 和 3
        BigDecimal ZDOdds = new BigDecimal(sysConfigMapper.selectByPrimaryKey(4l).getParamValue()); // 庄对 4
        BigDecimal ZOdds = new BigDecimal(sysConfigMapper.selectByPrimaryKey(5l).getParamValue()); // 庄 5
        odds.setXOdds(XOdds);
        odds.setXDOdds(XDOdds);
        odds.setHOdds(HOdds);
        odds.setZDOdds(ZDOdds);
        odds.setZOdds(ZOdds);
//        redisUtil.set(RedisKeysEnum.SYSTEM_ODDS_CONFIG.getKey(), odds, RedisKeysEnum.SYSTEM_ODDS_CONFIG.getExpireIn());

        return odds;
    }

    @Override
    public BaseResponse<List<TrendChartDataRespVO>> getTrendChartData() {
        List<BjlDrawRecords> bjlDrawRecordsList = bjlDrawRecordsMapper.selectTrendChartData();
        final int maxSize = Integer.min(bjlDrawRecordsList.size(), 60);
        bjlDrawRecordsList = bjlDrawRecordsList.subList(bjlDrawRecordsList.size() - maxSize ,bjlDrawRecordsList.size());
        List<TrendChartDataRespVO> trendChartDataRespVOList = new ArrayList<>();
        bjlDrawRecordsList.forEach(bjlDrawRecords -> {
            TrendChartDataRespVO trendChartDataRespVO = new TrendChartDataRespVO();
            BeanUtils.copyProperties(bjlDrawRecords, trendChartDataRespVO);
            BjlOpenprizeVideo bjlOpenprizeVideo = openprizeVideoMapper.selectByPrimaryKey(bjlDrawRecords.getAwardVideo());
            trendChartDataRespVO.setPlayerPair(bjlOpenprizeVideo.getPlayerPair());
            trendChartDataRespVO.setBankerPair(bjlOpenprizeVideo.getBankerPair());

            trendChartDataRespVOList.add(trendChartDataRespVO);
        });

        return BaseResponse.buildSuccess(trendChartDataRespVOList);
    }

    private  List<OddsVo> getOddsVo(){
        Odds odds = this.getOddsConfig();
        List<OddsVo> vos = new ArrayList<>();
        vos.add(new OddsVo(1,odds.getXOdds()));
        vos.add(new OddsVo(2,odds.getXDOdds()));
        vos.add(new OddsVo(3,odds.getHOdds()));
        vos.add(new OddsVo(4,odds.getZDOdds()));
        vos.add(new OddsVo(5,odds.getZOdds()));
        return  vos;

    }

}
