package com.lontyu.dwjwap.task;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.constants.PrizeResultEnum;
import com.lontyu.dwjwap.constants.SelectedSizeEnum;
import com.lontyu.dwjwap.dao.BjlDrawRecordsMapper;
import com.lontyu.dwjwap.dao.BjlOpenprizeVideoMapper;
import com.lontyu.dwjwap.dao.BjlOrderMapper;
import com.lontyu.dwjwap.dao.SysConfigMapper;
import com.lontyu.dwjwap.dto.Odds;
import com.lontyu.dwjwap.entity.BjlDrawRecords;
import com.lontyu.dwjwap.entity.BjlOpenprizeVideo;
import com.lontyu.dwjwap.entity.BjlOrder;
import com.lontyu.dwjwap.entity.SysConfig;
import com.lontyu.dwjwap.enums.SysConfigEnum;
import com.lontyu.dwjwap.service.BjlService;
import com.lontyu.dwjwap.socket.MessageEventHandler;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  当前期记录维护 - -定时器
 *  初始化当前期信息，创建当前期，维护投注时间，播放时间，开奖时间
 */
@Component("DrawRecordsTask")
public class DrawRecordsManagerTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    BjlDrawRecordsMapper bjlDrawRecordsMapper;


    @Autowired
    private BjlOpenprizeVideoMapper bjlOpenprizeVideoMapper;


    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    @Autowired
    private BjlService bjlService;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    MessageEventHandler messageEventHandler;

    /**
     * 初始化当前期信息，创建当前期，维护投注时间，播放时间，开奖时间
     */
    @Scheduled(cron = "*/2 * * * * ?")
    @Transactional
    public void  managerCurrentPeroid()  throws Exception{

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long startSeconds = getCurrentTimeSeconds(sdf.format(new Date()));  // 当前毫秒值
        logger.debug("当前期维护,定时任务开始....:"+sdf.format(new Date()));

        //检查当前系统是否在营业期间
        SysConfig openStatusConfig = sysConfigMapper.selectByParamKey(SysConfigEnum.SYS_STATUS.getKey());
        if(openStatusConfig==null || openStatusConfig.getParamValue()==null || !openStatusConfig.getParamValue().equals("1")){
            logger.info("当前系统未配置营业状态或处理停业状态： openStatusConfig:"+openStatusConfig==null? "": JSONObject.toJSONString(openStatusConfig));
            return ;
        }


        //1.初始化记录生成下注时间，需要考虑结果显示时间
       BjlDrawRecords records = bjlDrawRecordsMapper.selectActiveRecordForUpdate();
        Date currentDate = new Date();
        Calendar ca = Calendar.getInstance();
        if(records==null){
            // 获取待播放视频
            BjlOpenprizeVideo video = getNextVideo();

            //下单时长
            ca.setTime(new Date());
            ca.add(Calendar.SECOND, video.getOrderTimes());
            BjlDrawRecords newRecord = new BjlDrawRecords();
            newRecord.setStartOrderTime(new Date());
            newRecord.setEndOrderTime(ca.getTime());
            newRecord.setPreVideo1(video.getId());
            newRecord.setPreVideo2(video.getId());
            newRecord.setAddTime(new Date());
            newRecord.setStatus(0);
            newRecord.setDrawResult(0);
            newRecord.setStatisticalMethod(0);
            final SimpleDateFormat sdf2  = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            newRecord.setAwardPeriod(sdf2.format(new Date()));
            bjlDrawRecordsMapper.insertSelective(newRecord);
            logger.debug("生成当期记录："+ JSONObject.toJSONString(newRecord));
            sendSocketMessage();
            return;
        }

        //2 检查超过下注时间未生成等待发牌时间，生成等待发牌时间
        if(currentDate.compareTo(records.getEndOrderTime())>=0 && records.getStartWaitPlayTime()==null){

            //选择视频1
            BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(records.getPreVideo1());

            records.setStartWaitPlayTime(new Date());
            ca.setTime(new Date());
            ca.add(Calendar.SECOND,video.getCalOrderTimes());
            records.setEndWaitPlayTime(ca.getTime());
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
            logger.debug("设置当期 等待播放视频完成:"+ JSONObject.toJSONString(records));
            sendSocketMessage();
            return;
        }


        //2 检查超过下注时间未分配视频 则分配视频，生成视频播放时间
        if(records.getEndWaitPlayTime()!=null && currentDate.compareTo(records.getEndWaitPlayTime())>=0 && records.getStartPlayTime()==null){
            //根据投注结果选择视频
//            int drawResult = getDrawResult(records.getAwardPeriod());
//            int  videoId = drawResult== PrizeResultEnum.ZWin.getCode() ? records.getPreVideo1() : records.getPreVideo2();
//            BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(videoId);
            BjlOpenprizeVideo video = getPlayVideo(records.getAwardPeriod());
            records.setStartPlayTime(new Date());
            records.setDrawResult(video.getResultSign());
            ca.setTime(new Date());
            ca.add(Calendar.SECOND,video.getPlayTimes());
            records.setEndPlayTime(ca.getTime());
            records.setAwardVideo(video.getId());
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
           logger.debug("设置当期 播放视频完成:"+ JSONObject.toJSONString(records));
            sendSocketMessage();
            return;
        }

        //3 检查时间超过视频播放时间未分配结果显示时间，生成结果显示时间
        if(records.getEndPlayTime()!= null && currentDate.compareTo(records.getEndPlayTime())>=0 && records.getStartShowResultTime()==null){
            BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(records.getAwardVideo());
            ca.setTime(new Date());
            ca.add(Calendar.SECOND,video.getShowResultTimes());
            records.setStartShowResultTime(new Date());
            records.setEndShowResultTime(ca.getTime());
            logger.debug("设置当期开奖结果时间完成："+JSONObject.toJSONString(new Date()));
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
            sendSocketMessage();
            return;
        }

        if(records.getEndShowResultTime() != null && currentDate.compareTo(records.getEndShowResultTime())>= 0 &&records.getStatus()==0){
            records.setStatus(1);
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
            logger.debug("设置当期开奖结果时间完成："+JSONObject.toJSONString(new Date()));
//            sendSocketMessage();
            return;
        }

        logger.debug("当前期维护,定时任务结束....："+sdf.format(new Date()));
    }

    /**
     * 计算毫秒值
     *
     * @param startTime
     * @return
     */
    private long getCurrentTimeSeconds(String startTime) {
        String[] t = startTime.split(":");
        if (t.length != 3) {
            logger.error("时间格式有误，startTime =" + startTime);
            throw new InvalidParameterException();
        }

        return Integer.parseInt(t[0]) * 60 * 60 + Integer.parseInt(t[1]) * 60 + Integer.parseInt(t[2]);
    }

    /**
     *   计算订单平台需要支付额
     * @return
     */
    private BigDecimal getPlatformPayForOrderAmount(Odds odds, BjlOrder order) {
        //返还下注金额 *（1+赔率）
        return order.getBuyAmount().multiply(
                odds.getOddsValue(SelectedSizeEnum.getSelectedSizeEnum(order.getSelectedSize())).add(BigDecimal.ONE));
    }

    /**
     * 检验是否切换视频地址
     *
     * @return  // XWin(1,"闲胜"),ZWin(2,"庄胜"), HE(3,"和");
     */
    public int  getDrawResult (String currentPeriods)  throws Exception{
       // XWin(1,"闲胜"),ZWin(2,"庄胜"), HE(3,"和");
        // 2、计算当前这期 庄闲 卖家下注总改赔偿总金额
        Odds odds = bjlService.getOddsConfig();
        double allowRange = Double.parseDouble(sysConfigMapper.selectByPrimaryKey(6L).getParamValue()); // 允许误差值 6


        //支持庄家胜
        List<BjlOrder> zWinLists = bjlOrderMapper.getSupportList(currentPeriods,PrizeResultEnum.ZWin.getCode());

        //开庄家胜 平台需要支出金额
        BigDecimal zWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : zWinLists) {

            zWinAmount = zWinAmount.add(getPlatformPayForOrderAmount(odds,order));
        }
        List<BjlOrder> xWinLists = bjlOrderMapper.getSupportList(currentPeriods,PrizeResultEnum.XWin.getCode());

        //开闲胜平台支出金额
        BigDecimal xWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : xWinLists) {
            xWinAmount = xWinAmount.add(getPlatformPayForOrderAmount(odds,order));
        }

        //TODO 支持开和

        logger.info(currentPeriods+" 本期开庄胜金额平台需要支出金额:"+zWinAmount +"  开闲胜需要金额："+ xWinAmount);

        //开平台支出最少的
        BigDecimal midResult = zWinAmount.subtract(xWinAmount);
        if (midResult.intValue() > 0) {
           return  PrizeResultEnum.XWin.getCode();
        }
        //买庄家的多则开闲胜，相反开庄胜
    return  PrizeResultEnum.ZWin.getCode();

    }

    /**
     * 获取实际播放视频
     *
     * @param currentPeriods 当前期数
     * @return 实际播放视频
     */
    private BjlOpenprizeVideo getPlayVideo(String currentPeriods) {
        BjlDrawRecords bjlDrawRecords = bjlDrawRecordsMapper.selectByAwardPeriod(currentPeriods);
        BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(bjlDrawRecords.getPreVideo1());
        PrizeResultEnum originalResult = PrizeResultEnum.codeOf(video.getResultSign());
        boolean isXianDui = 1 == video.getPlayerPair();
        boolean isZhuangDui = 1 == video.getBankerPair();

        Odds odds = bjlService.getOddsConfig();
        // 获取庄家对应每种类型应出钱
        Map<Integer, BigDecimal> buyAmountMap = bjlOrderMapper.selectBuyAmountByPeriod(currentPeriods).stream().collect(
                Collectors.toMap(BjlOrder::getSelectedSize, BjlOrder::getBuyAmount));
        // 庄家待出金额
        Map<Integer, BigDecimal> payAmountMap = new HashMap<>();
        for(SelectedSizeEnum selectedSizeEnum : SelectedSizeEnum.values()) {
            BigDecimal payAmount = Optional.ofNullable(buyAmountMap.get(selectedSizeEnum.getCode())).map(
                    buyAmount -> buyAmount.multiply(odds.getOddsValue(selectedSizeEnum).add(BigDecimal.ONE))).orElse(BigDecimal.ZERO);
            payAmountMap.put(selectedSizeEnum.getCode(), payAmount);
        }

        // 获取允许误差金额
        BigDecimal allowRange = Optional.ofNullable(sysConfigMapper.selectByPrimaryKey(6L))
                .map(SysConfig::getParamValue).map(BigDecimal::new).orElse(BigDecimal.ZERO);
        // 允许单方结果出现对子最大支出金额
        BigDecimal maxPairPayAmount = Optional.ofNullable(sysConfigMapper.selectByParamKey("maxPairPayAmount"))
                .map(SysConfig::getParamValue).map(BigDecimal::new).orElse(BigDecimal.ZERO);
        // 预设视频结果减去缓冲金额
        switch (originalResult) {
            case ZWin:
                payAmountMap.put(SelectedSizeEnum.ZHUANG.getCode(), payAmountMap.get(SelectedSizeEnum.ZHUANG.getCode()).subtract(allowRange));
                break;
            case XWin:
                payAmountMap.put(SelectedSizeEnum.XIAN.getCode(), payAmountMap.get(SelectedSizeEnum.XIAN.getCode()).subtract(allowRange));
                break;
            case HE:
                payAmountMap.put(SelectedSizeEnum.HE.getCode(), payAmountMap.get(SelectedSizeEnum.HE.getCode()).subtract(allowRange));
                break;
            default:
                break;
        }

        // 根据庄家待出金额判断播放视频条件
        PrizeResultEnum finalResult;
        // 判断结果出现对子是否超额
        boolean isXianDuiOverflow = payAmountMap.get(SelectedSizeEnum.XIANDUI.getCode()).compareTo(maxPairPayAmount) > 0;
        boolean isZhuangDuiOverflow = payAmountMap.get(SelectedSizeEnum.ZHUANGDUI.getCode()).compareTo(maxPairPayAmount) > 0;
        // 增加强制和处理
        if (1 == bjlDrawRecords.getForceTie()) {
            finalResult = PrizeResultEnum.HE;
        } else {
            // step1：是否需要判断和
            boolean isJudgeHE = video.getResultSign() == PrizeResultEnum.HE.getCode();
            // step2：判断本局开牌结果
            // 判断庄闲结果
            finalResult = payAmountMap.get(SelectedSizeEnum.ZHUANG.getCode()).compareTo(
                    payAmountMap.get(SelectedSizeEnum.XIAN.getCode())) <= 0 ? PrizeResultEnum.ZWin : PrizeResultEnum.XWin;
            if (isJudgeHE) {
                if (finalResult == PrizeResultEnum.ZWin) {
                    finalResult = payAmountMap.get(SelectedSizeEnum.ZHUANG.getCode()).compareTo(
                            payAmountMap.get(SelectedSizeEnum.HE.getCode())) <= 0 ? PrizeResultEnum.ZWin : PrizeResultEnum.HE;
                } else {
                    finalResult = payAmountMap.get(SelectedSizeEnum.XIAN.getCode()).compareTo(
                            payAmountMap.get(SelectedSizeEnum.HE.getCode())) <= 0 ? PrizeResultEnum.XWin : PrizeResultEnum.HE;
                }
            }
        }

        // 判断原始视频是否合适
        if (originalResult == finalResult && !(isXianDuiOverflow && isXianDui) && !(isZhuangDuiOverflow && isZhuangDui)) {
            return video;
        }

        // 如果原视频不合适，
        BjlOpenprizeVideo resultVideo = null;
        Integer deingerNum = video.getDeingerNum();
        // 考虑庄闲对
        do {
            Integer id = deingerNum.intValue() != video.getDeingerNum() ? null : video.getId();
            BjlOpenprizeVideo v = bjlOpenprizeVideoMapper.selectMappingVideo(id, deingerNum, finalResult.getCode(),
                    isXianDuiOverflow ? 0 : null, isZhuangDuiOverflow ? 0 : null);
            if (null != v) {
                resultVideo = v;
                break;
            }

            deingerNum = bjlOpenprizeVideoMapper.selectNextDeingerNum(deingerNum);
        } while (deingerNum.intValue() != video.getDeingerNum());

        if (null == resultVideo) {
            // 不考虑庄闲对
            do {
                Integer id = deingerNum.intValue() != video.getDeingerNum() ? null : video.getId();
                BjlOpenprizeVideo v = bjlOpenprizeVideoMapper.selectMappingVideo(id, deingerNum, finalResult.getCode(),
                        null, null);
                if (null != v) {
                    resultVideo = v;
                    break;
                }

                deingerNum = bjlOpenprizeVideoMapper.selectNextDeingerNum(deingerNum);
            } while (deingerNum.intValue() != video.getDeingerNum());
        }

        resultVideo = null == resultVideo ? video : resultVideo;
        setCurrentDeingerWithVideoConfig(resultVideo);
        return resultVideo;
    }

    private void sendSocketMessage(){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit()
            {
               messageEventHandler.broadcastPrizeEvent();
            }
        } );
    }

    /**
     * 获取下一个播放视频
     *
     * @return 播放员编号
     */
    private BjlOpenprizeVideo getNextVideo() {
        // 当前播放员及播放视频
        final String currentDeingerWithVideoKey = "currentDeingerWithVideo";
        SysConfig currentDeingerWithVideoConfig = sysConfigMapper.selectByParamKey(currentDeingerWithVideoKey);

        BjlOpenprizeVideo video;
        try {
            // 当前播放员及播放视频
            String[] array = currentDeingerWithVideoConfig.getParamValue().split("_");
            int deingerNum = Integer.parseInt(array[0]);
            int videoId = Integer.parseInt(array[1]);

            // 获取当前播放员下一个视频；若没有，获取下个播放员第一个视频
            video = bjlOpenprizeVideoMapper.selectNextVideo(deingerNum, videoId);
            if (null == video) {
                deingerNum = bjlOpenprizeVideoMapper.selectNextDeingerNum(deingerNum);
                video = bjlOpenprizeVideoMapper.selectNextVideo(deingerNum, null);
            }
        } catch (Exception e) {
            video = bjlOpenprizeVideoMapper.selectRandomVideo(null, null);
        }

        setCurrentDeingerWithVideoConfig(video);
        return video;
    }

    private void setCurrentDeingerWithVideoConfig(BjlOpenprizeVideo video) {
        String paramValue = video.getDeingerNum() + "_" + video.getId();

        // 当前播放员及播放视频
        final String currentDeingerWithVideoKey = "currentDeingerWithVideo";
        SysConfig currentDeingerWithVideoConfig = sysConfigMapper.selectByParamKey(currentDeingerWithVideoKey);
        // 修改配置
        if (null == currentDeingerWithVideoConfig) {
            currentDeingerWithVideoConfig = new SysConfig();
            currentDeingerWithVideoConfig.setParamKey(currentDeingerWithVideoKey);
            currentDeingerWithVideoConfig.setParamValue(paramValue);
            currentDeingerWithVideoConfig.setStatus((byte) 1);
            sysConfigMapper.insertSelective(currentDeingerWithVideoConfig);
        } else {
            currentDeingerWithVideoConfig.setParamValue(paramValue);
            sysConfigMapper.updateByPrimaryKeySelective(currentDeingerWithVideoConfig);
        }
    }
}
