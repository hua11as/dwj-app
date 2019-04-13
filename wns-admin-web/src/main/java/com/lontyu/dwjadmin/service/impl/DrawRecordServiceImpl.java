package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.BjlDrawRecordsMapper;
import com.lontyu.dwjadmin.dao.BjlOrderMapper;
import com.lontyu.dwjadmin.entity.BjlDrawRecords;
import com.lontyu.dwjadmin.service.DrawRecordService;
import com.lontyu.dwjadmin.vo.DrawRecodeRespVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author as
 * @desc
 * @date 2019/1/13
 */
@Service
public class DrawRecordServiceImpl extends ServiceImpl<BjlDrawRecordsMapper, BjlDrawRecords> implements DrawRecordService {

    @Autowired
    private BjlOrderMapper bjlOrderMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String awardPeriod = (String) params.get("awardPeriod");

        Page<BjlDrawRecords> page = this.selectPage(
                new Query<BjlDrawRecords>(params).getPage(),
                new EntityWrapper<BjlDrawRecords>()
                        .eq(StringUtils.isNotBlank(awardPeriod), "award_period", awardPeriod)
                        .orderBy("award_period", false)
        );

        List<BjlDrawRecords> records = new ArrayList<>();
        page.getRecords().forEach(record -> {
            DrawRecodeRespVO target = Optional.ofNullable(bjlOrderMapper.statPeriodBetAmount(record.getAwardPeriod()))
                    .orElse(new DrawRecodeRespVO());
            BeanUtils.copyProperties(record, target);
            records.add(target);
        });
        page.setRecords(records);

        return new PageUtils(page);
    }

    @Override
    public void forceTie(String period) {
        if (StringUtils.isBlank(period)) {
            throw new RRException("开奖期数不能为空");
        }

        BjlDrawRecords bjlDrawRecords = this.selectOne(new EntityWrapper<BjlDrawRecords>()
                .eq(true, "award_period", period));
        if (null == bjlDrawRecords) {
            throw new RRException("未找到对应期数");
        }
        if (null != bjlDrawRecords.getAwardVideo()) {
            throw new RRException("该期已生成开奖视频，不允许设置强制开和");
        }
        if (1 == bjlDrawRecords.getForceTie()) {
            throw new RRException("该期已设置强制开和，无需重复设置");
        }
        BjlDrawRecords updateRecord = new BjlDrawRecords();
        updateRecord.setId(bjlDrawRecords.getId());
        updateRecord.setForceTie(1);
        updateRecord.setUpdateTime(new Date());
        this.updateById(updateRecord);
    }
}
