package com.lontyu.dwjadmin.task;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjadmin.constants.SysParamEnum;
import com.lontyu.dwjadmin.dao.*;
import com.lontyu.dwjadmin.entity.*;
import com.lontyu.dwjadmin.service.SysConfigService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  当前期记录维护 - -定时器
 *  初始化当前期信息，创建当前期，维护投注时间，播放时间，开奖时间
 */
@Component("DrawRecordsTask")
public class DrawRecordsManagerTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private BjlEndChipinMapper bjlEndChipinMapper;

    @Autowired
    private BjlDrawRecordsMapper bjlDrawRecordsMapper;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysConfigDao sysconfigDao;

    @Autowired
    private BjlOpenprizeVideoMapper bjlOpenprizeVideoMapper;


    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    /**
     * 初始化当前期信息，创建当前期，维护投注时间，播放时间，开奖时间
     */
//    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void  managerCurrentPeroid()  throws Exception{

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long startSeconds = getCurrentTimeSeconds(sdf.format(new Date()));  // 当前毫秒值
        logger.info("当前期维护,定时任务开始....:"+sdf.format(new Date()));

        //1.初始化记录生成下注时间，需要考虑结果显示时间
       BjlDrawRecords records = bjlDrawRecordsMapper.selectActiveRecordForUpdate();
        Date currentDate = new Date();
        Calendar ca = Calendar.getInstance();
        if(records==null){
            // 随机选同组视频2个一庄胜一闲胜
            //庄家胜
            BjlOpenprizeVideo video1=bjlOpenprizeVideoMapper.selectRandomVideo(0);
            //闲家胜
            BjlOpenprizeVideo video2=bjlOpenprizeVideoMapper.selectRandomVideo(1);

            //下单时长
            ca.setTime(new Date());
            ca.add(Calendar.SECOND, video1.getOrderTimes());
            BjlDrawRecords newRecord = new BjlDrawRecords();
            newRecord.setStartOrderTime(new Date());
            newRecord.setEndOrderTime(ca.getTime());
            newRecord.setPreVideo1(video1.getId());
            newRecord.setPreVideo2(video2.getId());
            newRecord.setAddTime(new Date());
            newRecord.setStatus(0);
            newRecord.setDrawResult(0);
            newRecord.setStatisticalMethod(0);
            final SimpleDateFormat sdf2  = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            newRecord.setAwardPeriod(sdf2.format(new Date()));
            bjlDrawRecordsMapper.insertSelective(newRecord);
            logger.info("生成当期记录："+ JSONObject.toJSONString(newRecord));
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
            logger.info("设置当期 等待播放视频完成:"+ JSONObject.toJSONString(records));
            //todo 推送前
            return;
        }


        //2 检查超过下注时间未分配视频 则分配视频，生成视频播放时间
        if(records.getEndWaitPlayTime()!=null && currentDate.compareTo(records.getEndWaitPlayTime())>=0 && records.getStartPlayTime()==null){
            //根据投注结果选择视频
            int drawResult = getDrawResult(records.getAwardPeriod());
            int  videoId= drawResult== 0 ? records.getPreVideo1() : records.getPreVideo2();
            BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(videoId);
            records.setStartPlayTime(new Date());
            records.setDrawResult(drawResult);
            ca.setTime(new Date());
            ca.add(Calendar.SECOND,video.getPlayTimes());
            records.setEndPlayTime(ca.getTime());
            records.setAwardVideo(video.getId());
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
           logger.info("设置当期 播放视频完成:"+ JSONObject.toJSONString(records));
           //todo 推送前端播放视频ID与url
            return;
        }

        //3 检查时间超过视频播放时间未分配结果显示时间，生成结果显示时间
        if(records.getEndPlayTime()!= null && currentDate.compareTo(records.getEndPlayTime())>=0 && records.getStartShowResultTime()==null){

            BjlOpenprizeVideo video = bjlOpenprizeVideoMapper.selectByPrimaryKey(records.getAwardVideo());
            ca.setTime(new Date());
            ca.add(Calendar.SECOND,video.getShowResultTimes());
            records.setStartShowResultTime(new Date());
            records.setEndShowResultTime(ca.getTime());
            logger.info("设置当期开奖结果时间完成："+JSONObject.toJSONString(new Date()));
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
            return;
        }

        if(records.getEndShowResultTime() != null && currentDate.compareTo(records.getEndShowResultTime())>= 0 &&records.getStatus()==0){
            records.setStatus(1);
            bjlDrawRecordsMapper.updateByPrimaryKeySelective(records);
            logger.info("设置当期开奖结果时间完成："+JSONObject.toJSONString(new Date()));
            return;
        }

        logger.info("当前期维护,定时任务结束....："+sdf.format(new Date()));
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
        if (selectedSize == SysParamEnum.XIAN.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(XOdds)));
        } else if (selectedSize == SysParamEnum.XIANDUI.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(XDOdds)));
        } else if (selectedSize == SysParamEnum.HE.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(HOdds)));
        } else if (selectedSize == SysParamEnum.ZHUANGDUI.getCode()) {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(ZDOdds)));
        } else {
            zWinAmount = zWinAmount.add(buyAmount.multiply(BigDecimal.valueOf(ZOdds)));
        }
        return zWinAmount;
    }

    /**
     * 检验是否切换视频地址
     *
     * @return 0:庄家胜，1：闲家胜
     */
    public int  getDrawResult (String currentPeriods)  throws Exception{

        // 2、计算当前这期 庄闲 卖家下注总改赔偿总金额
        List<SysConfigEntity> sysConfigs = sysconfigDao.selectNeedRecords();
        if (Objects.isNull(sysConfigs) || sysConfigs.size() != 6) {
           throw new Exception("配置错误，请检查赔率设置");
        }

        double XOdds = Double.parseDouble(sysConfigs.get(0).getParamValue()); // 闲 1
        double XDOdds = Double.parseDouble(sysConfigs.get(1).getParamValue()); // 闲对 2
        double HOdds = Double.parseDouble(sysConfigs.get(2).getParamValue()); // 和 3
        double ZDOdds = Double.parseDouble(sysConfigs.get(3).getParamValue()); // 庄对 4
        double ZOdds = Double.parseDouble(sysConfigs.get(4).getParamValue()); // 庄 5
        double allowRange = Double.parseDouble(sysConfigs.get(5).getParamValue()); // 允许误差值 6


        //支持庄家胜
        List<BjlOrder> zWinLists = bjlOrderMapper.getSupportList(currentPeriods,0);

        BigDecimal zWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : zWinLists) {
            int selectedSize = order.getSelectedSize().intValue();
            BigDecimal buyAmount = order.getBuyAmount();
            zWinAmount = getTotalAmount(XOdds, XDOdds, HOdds, ZDOdds, ZOdds,
                    zWinAmount, selectedSize, buyAmount);
        }
        List<BjlOrder> xWinLists = bjlOrderMapper.getSupportList(currentPeriods,1);
        BigDecimal xWinAmount = BigDecimal.ZERO;
        for (BjlOrder order : xWinLists) {
            int selectedSize = order.getSelectedSize().intValue();
            BigDecimal buyAmount = order.getBuyAmount();
            xWinAmount = getTotalAmount(XOdds, XDOdds, HOdds, ZDOdds, ZOdds,
                    xWinAmount, selectedSize, buyAmount);
        }
        logger.info(currentPeriods+" 期庄胜金额:"+zWinAmount +"  闲胜金额："+ xWinAmount);

        boolean zWin = false;
        BigDecimal midResult = zWinAmount.subtract(xWinAmount);
        if (midResult.intValue() > 0) {
            zWin = true;
        }
        //买庄家的多则开闲胜，相反开庄胜
    return  zWin ? 1 : 0;

    }
}
