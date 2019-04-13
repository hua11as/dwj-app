package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.BjlOpenprizeVideoMapper;
import com.lontyu.dwjadmin.entity.BjlOpenprizeVideo;
import com.lontyu.dwjadmin.service.OpenPrizeVideosService;
import com.lontyu.dwjadmin.util.DwjUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


/**
 * vip用户
 */
@Service("openVideosService")
public class OpenPrizeVideosServiceImpl extends ServiceImpl<BjlOpenprizeVideoMapper, BjlOpenprizeVideo> implements OpenPrizeVideosService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String id = (String) params.get("id");
        String deingerNum = (String) params.get("deingerNum");

        Page<BjlOpenprizeVideo> page = this.selectPage(
                new Query<BjlOpenprizeVideo>(params).getPage(),
                new EntityWrapper<BjlOpenprizeVideo>()
                        .eq(StringUtils.isNotBlank(id), "id", id)
                        .eq(StringUtils.isNotBlank(deingerNum), "deinger_num", deingerNum)
                        .orderBy("deinger_num", true)
        );
        return new PageUtils(page);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(BjlOpenprizeVideo video) {
        video.setAddTime(new Date());
        this.insert(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BjlOpenprizeVideo video) {

        this.updateById(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] videoIds) {
        //删除数据
        this.deleteBatchIds(Arrays.asList(videoIds));
    }

    @Override
    @Transactional
    public void upload(MultipartFile file) {
        Sheet sheet;
        int totalRowNum;
        try {
            Workbook wookbook = new XSSFWorkbook(file.getInputStream());
            sheet = wookbook.getSheetAt(0);
            totalRowNum = sheet.getLastRowNum();
        } catch (IOException e) {
            throw new RRException("系统异常");
        }

        BjlOpenprizeVideo video;
        for (int i = 1; i <= totalRowNum; i++) {
            Row row = sheet.getRow(i);
            video = new BjlOpenprizeVideo();
            video.setDeingerNum((int) row.getCell(0).getNumericCellValue());
            video.setResultSign((int) row.getCell(1).getNumericCellValue());
            video.setBankerPoint(row.getCell(2).getStringCellValue());
            video.setBankerPointColor(row.getCell(3).getStringCellValue());
            video.setPlayerPoint(row.getCell(4).getStringCellValue());
            video.setPlayerPointColor(row.getCell(5).getStringCellValue());
            video.setLinkAdress(row.getCell(6).getStringCellValue());
            video.setRemark(Optional.ofNullable(row.getCell(7)).map(Cell::getStringCellValue).orElse(""));
            video.setPlayTimes((int) row.getCell(8).getNumericCellValue());
            video.setShowResultTimes((int) row.getCell(9).getNumericCellValue());
            video.setCalOrderTimes(0);
            video.setOrderTimes(30);
            video.setTotalPlayTimes(video.getPlayTimes() + video.getShowResultTimes() + video.getCalOrderTimes() + video.getOrderTimes());
            video.setStatus(0);
            video.setBankerPair(DwjUtils.checkDoublePoint(video.getBankerPoint()) ? 1 : 0);
            video.setPlayerPair(DwjUtils.checkDoublePoint(video.getPlayerPoint()) ? 1 : 0);
            this.save(video);
        }
    }
}
