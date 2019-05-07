package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统当前信息，返回当前是否营业，如营业返回当前期相关信息
 *
 * @author xxx
 * @createTime 2018-07-20 09:59:25
 */
@ApiModel
public class CurrentInfoVo {

    public static enum OpenStatus{
        OPEN(1,"营业"),CLOSE(0,"停业");
        int code;
        String desc;
        OpenStatus(int code,String desc){
            this.code= code;
            this.desc = desc;

        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static enum CurrentStatus{
        ORDER_TIME(0,"下注中"),WAIT_PLAY_TIME(1,"等待发牌中"),PLAY_TIME(2,"发牌中"),SHOW_TIME(3,"开奖结果展示中");
        int code;
        String desc;
        CurrentStatus(int code,String desc){
            this.code= code;
            this.desc = desc;

        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    //当前系统状态 0：停业 1：营业
    @ApiModelProperty(value="当前系统状态 0：停业 1：营业")
    private Integer openStatus;

    //当前开奖期  20180902000001
    @ApiModelProperty(value="当前开奖期 20180902000001")
    private String  currentPeriod;
    //  ORDER_TIME(0,"下注中"),WAIT_PLAY_TIME(1,"等待发牌中"),PLAY_TIME(2,"发牌中"),SHOW_TIME(3,"开奖结果展示中")
    @ApiModelProperty(value="0:下注中,1:等待发牌中,2:发牌中，3:开奖结果展示中")
    private Integer currentStatus;
    @ApiModelProperty(value="状态描述")
    private String currentStatusDesc;
    //开始下注时间
    @ApiModelProperty(value="开始下注时间")
    private Long startOrderTime;
    //截止下注时间
    @ApiModelProperty(value="截止下注时间")
    private Long endOrderTime;

    //开始计算投资结果
    @ApiModelProperty(value="开始计算投注结果时间")
    private Long startWaitPlayTime;
    //截止发牌时间
    @ApiModelProperty(value="截止计算投注结果时间")
    private Long endWaitPlayTime;

    //预计播放的视频1闲胜2庄家胜（只在下注期间返回）
    @ApiModelProperty(value="预播放视频ID1")
    private Integer preVideo1Id;
    @ApiModelProperty(value="预播放视频URL1")
    private String preVideo1URL;
    @ApiModelProperty(value="预播放视频ID2")
    private Integer preVideo2Id;
    @ApiModelProperty(value="预播放视频URL2")
    private String preVideo2URL;

    //视频播放中视频播放信息
    @ApiModelProperty(value="开始发牌时间")
    private Long startPlayTime;
    @ApiModelProperty(value="发牌结束时间")
    private Long endPlayTime;
    @ApiModelProperty(value="本期播放视频ID")
    private Integer  playVideoId;
    @ApiModelProperty(value="本期播放视频URL")
    private String  playVideo1URL;

    //结果展示中 当期开奖结果
    @ApiModelProperty(value="本期开奖结果显示开始时间")
    private Long startShowResultTime;
    @ApiModelProperty(value="本期开奖结果显示结束时间")
    private Long endShowResultTime;
    //当前期开将结果  UNKNOWN(0,"未知"),BANKER_WIN(1,"庄家胜"),CLIENT_WIN(2,"闲家胜");
    @ApiModelProperty(value="当前期开将结果  0:初始状态 1：闲胜 2：庄胜 3：和")
    private Integer currentPeroidResult;

    @ApiModelProperty("闲家是否成对 0：否 1：是")
    private Integer playerPair;

    @ApiModelProperty("庄家是否成对 0：否 1：是")
    private Integer bankerPair;

    @ApiModelProperty(value="庄家发牌结果 1，2，3，4, ...13 分别对应 1-K")
    private String bankResult;

    @ApiModelProperty(value="庄家发牌颜色 1，2，3，4 分别对应 黑红梅方")
    private String bankResultColor;


    @ApiModelProperty(value = "闲家发牌结果 1，2，3，4，... 13 分别对应 1-K")
    private String clientResult;

    @ApiModelProperty(value = "闲家发牌颜色 1，2，3，4 分别对应 黑红梅方")
    private String clientResultColor;

    //系统当前时间
    @ApiModelProperty(value="系统当前时间")
    private Long sysCurrentTime;
    @ApiModelProperty(value="投资赔率配置")
    List<OddsVo> odds;

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public String getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(String currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Long getStartOrderTime() {
        return startOrderTime;
    }

    public void setStartOrderTime(Long startOrderTime) {
        this.startOrderTime = startOrderTime;
    }

    public Long getEndOrderTime() {
        return endOrderTime;
    }

    public void setEndOrderTime(Long endOrderTime) {
        this.endOrderTime = endOrderTime;
    }

    public Integer getPreVideo1Id() {
        return preVideo1Id;
    }

    public void setPreVideo1Id(Integer preVideo1Id) {
        this.preVideo1Id = preVideo1Id;
    }

    public String getPreVideo1URL() {
        return preVideo1URL;
    }

    public void setPreVideo1URL(String preVideo1URL) {
        this.preVideo1URL = preVideo1URL;
    }

    public Integer getPreVideo2Id() {
        return preVideo2Id;
    }

    public void setPreVideo2Id(Integer preVideo2Id) {
        this.preVideo2Id = preVideo2Id;
    }

    public String getPreVideo2URL() {
        return preVideo2URL;
    }

    public void setPreVideo2URL(String preVideo2URL) {
        this.preVideo2URL = preVideo2URL;
    }

    public Long getStartPlayTime() {
        return startPlayTime;
    }

    public void setStartPlayTime(Long startPlayTime) {
        this.startPlayTime = startPlayTime;
    }

    public Long getEndPlayTime() {
        return endPlayTime;
    }

    public void setEndPlayTime(Long endPlayTime) {
        this.endPlayTime = endPlayTime;
    }

    public Integer getPlayVideoId() {
        return playVideoId;
    }

    public void setPlayVideoId(Integer playVideoId) {
        this.playVideoId = playVideoId;
    }

    public String getPlayVideo1URL() {
        return playVideo1URL;
    }

    public void setPlayVideo1URL(String playVideo1URL) {
        this.playVideo1URL = playVideo1URL;
    }

    public Long getStartShowResultTime() {
        return startShowResultTime;
    }

    public void setStartShowResultTime(Long startShowResultTime) {
        this.startShowResultTime = startShowResultTime;
    }

    public Long getEndShowResultTime() {
        return endShowResultTime;
    }

    public void setEndShowResultTime(Long endShowResultTime) {
        this.endShowResultTime = endShowResultTime;
    }

    public Integer getCurrentPeroidResult() {
        return currentPeroidResult;
    }

    public void setCurrentPeroidResult(Integer currentPeroidResult) {
        this.currentPeroidResult = currentPeroidResult;
    }

    public Long getSysCurrentTime() {
        return sysCurrentTime;
    }

    public void setSysCurrentTime(Long sysCurrentTime) {
        this.sysCurrentTime = sysCurrentTime;
    }

    public Long getStartWaitPlayTime() {
        return startWaitPlayTime;
    }

    public void setStartWaitPlayTime(Long startWaitPlayTime) {
        this.startWaitPlayTime = startWaitPlayTime;
    }

    public Long getEndWaitPlayTime() {
        return endWaitPlayTime;
    }

    public void setEndWaitPlayTime(Long endWaitPlayTime) {
        this.endWaitPlayTime = endWaitPlayTime;
    }

    public String getCurrentStatusDesc() {
        return currentStatusDesc;
    }

    public void setCurrentStatusDesc(String currentStatusDesc) {
        this.currentStatusDesc = currentStatusDesc;
    }

    public List<OddsVo> getOdds() {
        return odds;
    }

    public void setOdds(List<OddsVo> odds) {
        this.odds = odds;
    }

    public String getBankResult() {
        return bankResult;
    }

    public void setBankResult(String bankResult) {
        this.bankResult = bankResult;
    }

    public String getClientResult() {
        return clientResult;
    }

    public void setClientResult(String clientResult) {
        this.clientResult = clientResult;
    }

    public String getBankResultColor() {
        return bankResultColor;
    }

    public void setBankResultColor(String bankResultColor) {
        this.bankResultColor = bankResultColor;
    }

    public String getClientResultColor() {
        return clientResultColor;
    }

    public void setClientResultColor(String clientResultColor) {
        this.clientResultColor = clientResultColor;
    }

    public Integer getPlayerPair() {
        return playerPair;
    }

    public void setPlayerPair(Integer playerPair) {
        this.playerPair = playerPair;
    }

    public Integer getBankerPair() {
        return bankerPair;
    }

    public void setBankerPair(Integer bankerPair) {
        this.bankerPair = bankerPair;
    }
}
