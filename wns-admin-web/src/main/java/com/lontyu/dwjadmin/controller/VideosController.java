package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.common.validator.ValidatorUtils;
import com.lontyu.dwjadmin.common.validator.group.UpdateGroup;
import com.lontyu.dwjadmin.constants.PrizeResultEnum;
import com.lontyu.dwjadmin.entity.BjlOpenprizeVideo;
import com.lontyu.dwjadmin.service.OpenPrizeVideosService;
import com.lontyu.dwjadmin.util.DwjUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 开奖视频
 */
@RestController
@RequestMapping("/openprize/video")
public class VideosController extends AbstractController {

    @Autowired
    OpenPrizeVideosService openVideosService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = openVideosService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 用户信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        BjlOpenprizeVideo video = openVideosService.selectById(id);
        return R.ok().put("video", video);
    }

    /**
     * 保存定时任务
     */
    @RequestMapping("/save")
    public R save(@RequestBody BjlOpenprizeVideo video) {
        ValidatorUtils.validateEntity(video);
        video.setBankerPair(DwjUtils.checkDoublePoint(video.getBankerPoint()) ? 1 : 0);
        video.setPlayerPair(DwjUtils.checkDoublePoint(video.getPlayerPoint()) ? 1 : 0);
        openVideosService.save(video);
        return R.ok();
    }

    /**
     * 修改用户
     */
    @RequestMapping("/update")
    public R update(@RequestBody BjlOpenprizeVideo video) {
        ValidatorUtils.validateEntity(video, UpdateGroup.class);
        video.setBankerPair(DwjUtils.checkDoublePoint(video.getBankerPoint()) ? 1 : 0);
        video.setPlayerPair(DwjUtils.checkDoublePoint(video.getPlayerPoint()) ? 1 : 0);
        openVideosService.updateAllColumnById(video);

        return R.ok();
    }

    /**
     * 删除用户
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] videoIds) {
        openVideosService.deleteBatchIds(Arrays.asList(videoIds));
        return R.ok();
    }

    @PostMapping("upload")
    public R upload(MultipartFile file) {
        openVideosService.upload(file);
        return R.ok();
    }

    @PostMapping("refreshPointPair")
    public R refreshPointPair() {
        List<BjlOpenprizeVideo> videoList = openVideosService.selectList(null);
        videoList.forEach(video -> {
            video.setBankerPair(DwjUtils.checkDoublePoint(video.getBankerPoint()) ? 1 : 0);
            video.setPlayerPair(DwjUtils.checkDoublePoint(video.getPlayerPoint()) ? 1 : 0);
            // 庄家牌
            String bankerPointStr = video.getBankerPoint();
            // 闲家牌
            String playerPointStr = video.getPlayerPoint();
            String[] bankerArray = bankerPointStr.split(",");
            String[] playerArray = playerPointStr.split(",");
            int bankerPoint = Arrays.stream(bankerArray).mapToInt(Integer::parseInt).map(point -> point >= 10 ? 0 : point).sum();
            int playerPoint = Arrays.stream(playerArray).mapToInt(Integer::parseInt).map(point -> point >= 10 ? 0 : point).sum();
            bankerPoint = bankerPoint % 10;
            playerPoint = playerPoint % 10;
            int resultSign;
            if (bankerPoint > playerPoint) {
                resultSign = PrizeResultEnum.ZWin.getCode();
            } else if(bankerPoint == playerPoint) {
                resultSign = PrizeResultEnum.HE.getCode();
            } else {
                resultSign = PrizeResultEnum.XWin.getCode();
            }
            if (resultSign != video.getResultSign()) {
                video.setResultSign(resultSign);
                video.setRemark("刷新修改结果");
            }

            openVideosService.updateById(video);
        });
        return R.ok();
    }

}
