package com.lontyu.dwjadmin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.exception.RRException;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.constants.MoneyRecordTypeEnum;
import com.lontyu.dwjadmin.dao.VipMemberMapper;
import com.lontyu.dwjadmin.dao.WechatMemberMapper;
import com.lontyu.dwjadmin.dao.WechatRechargeQrcodeHisMapper;
import com.lontyu.dwjadmin.dao.WechatRechargeQrcodeMapper;
import com.lontyu.dwjadmin.entity.*;
import com.lontyu.dwjadmin.property.FileUploadProperties;
import com.lontyu.dwjadmin.service.MoneyRecordService;
import com.lontyu.dwjadmin.service.RechargeQrcodeService;
import com.lontyu.dwjadmin.service.SysConfigService;
import com.lontyu.dwjadmin.vo.RechargeCallbackReqVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author as
 * @desc
 * @date 2018/12/21
 */
@Service
public class RechargeQrcodeServiceImpl extends ServiceImpl<WechatRechargeQrcodeMapper, WechatRechargeQrcode> implements RechargeQrcodeService {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private WechatRechargeQrcodeHisMapper wechatRechargeQrcodeHisMapper;

    @Autowired
    private VipMemberMapper vipMemberMapper;

    @Autowired
    private MoneyRecordService moneyRecordService;

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String amount = (String) params.get("amount");
        String nickName = (String)params.get("nickName");
        Integer nickNameVipId = null;
        if (StringUtils.isNotBlank(nickName)) {
            WechatMember wmParam = new WechatMember();
            wmParam.setNickName(nickName);
            WechatMember wechatMember = wechatMemberMapper.selectOne(wmParam);
            nickNameVipId = null != wechatMember ? wechatMember.getVipId() : -1;
        }

        Page<Map<String, Object>> page = this.selectMapsPage(
                new Query<WechatRechargeQrcode>(params).getPage(),
                new EntityWrapper<WechatRechargeQrcode>()
                        .eq(StringUtils.isNotBlank(amount), "amount", amount)
                        .eq(null != nickNameVipId, "bind_user_id", nickNameVipId)
                        .eq(true, "del_flag", "0")
                        .orderBy("create_time", false)
        );
        page.getRecords().forEach(record -> {
            if (null == record.get("bindUserId")) {
                return;
            }
            Integer userId = Integer.parseInt(record.get("bindUserId").toString());
            WechatMember wechatMember = wechatMemberMapper.selectById(userId);
            record.put("bindNickName", wechatMember.getNickName());
        });

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void deleteByIds(Long[] ids) {
        if (!ArrayUtils.isEmpty(ids)) {
            this.baseMapper.selectByIdsForUpdate(ids);
            this.baseMapper.deleteByIds(ids);
        }
    }

    @Override
    @Transactional
    public void recoveryRechargeQrcode() {
        List<WechatRechargeQrcode> qrcodeList = this.baseMapper.selectUsedQrcodeForUpdate();
        // 获取系统配置超时时间
        final String rechargeErCodeOvertimeKey = "rechargeErCodeOvertime";
        final Integer defaultOvertime = 600;
        Integer rechargeErCodeOvertime = Optional.ofNullable(sysConfigService.getValue(rechargeErCodeOvertimeKey)).
                map(Integer::parseInt).orElse(defaultOvertime);
        DateTime now = DateTime.now();
        qrcodeList.forEach(qrcode -> {
            Date bindTime = qrcode.getBindTime();
            if (null != bindTime) {
                DateTime time = new DateTime(bindTime).plusSeconds(rechargeErCodeOvertime);
                if (time.isAfter(now)) {
                    return;
                }
            }

            // 增加历史记录
            this.addRechargeQrcodeHis(qrcode, null);

            // 将使用状态清空
            qrcode.setStatus(0);
            qrcode.setBindTime(null);
            qrcode.setBindUserId(null);
            this.baseMapper.updateAllColumnById(qrcode);
        });
    }

    @Override
    public void addRechargeQrcodeHis(WechatRechargeQrcode wechatRechargeQrcode, String callback) {
        WechatRechargeQrcodeHis wechatRechargeQrcodeHis = new WechatRechargeQrcodeHis();
        BeanUtils.copyProperties(wechatRechargeQrcode, wechatRechargeQrcodeHis);
        wechatRechargeQrcodeHis.setId(null);
        wechatRechargeQrcodeHis.setOriginalId(wechatRechargeQrcode.getId());
        wechatRechargeQrcodeHis.setCallBack(callback);
        wechatRechargeQrcodeHisMapper.insert(wechatRechargeQrcodeHis);
    }

    @Override
    @Transactional
    public void rechargeSuccess(RechargeCallbackReqVO reqVO) {
        WechatRechargeQrcode wechatRechargeQrcode = this.baseMapper.selectByRealAmountForUpdate(reqVO.getMoney());
        if (null == wechatRechargeQrcode || 1 != wechatRechargeQrcode.getStatus()) {
            return;
        }

        // 给用户充值
        VipMember member = vipMemberMapper.selectByPrimaryKeyForUpdate(wechatRechargeQrcode.getBindUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("amount", wechatRechargeQrcode.getAmount());
        params.put("id", member.getId());
        params.put("version", member.getVersion());
        vipMemberMapper.updateAoumt(params);

        // 插入流水
        Date now = new Date();
        MoneyRecord moneyRecord = new MoneyRecord();
        moneyRecord.setVipId(wechatRechargeQrcode.getBindUserId());
        moneyRecord.setAmount(new BigDecimal(wechatRechargeQrcode.getAmount()));
        moneyRecord.setRemark("用户充值");
        moneyRecord.setStatus(1);
        moneyRecord.setType(MoneyRecordTypeEnum.RECHARGE.getCode());
        moneyRecord.setCreateTime(now);
        moneyRecord.setOrderId(reqVO.getOrder());
        moneyRecordService.insert(moneyRecord);

        // 增加历史记录
        this.addRechargeQrcodeHis(wechatRechargeQrcode, JSONObject.toJSONString(reqVO));

        // 将使用状态清空
        wechatRechargeQrcode.setStatus(0);
        wechatRechargeQrcode.setBindTime(null);
        wechatRechargeQrcode.setBindUserId(null);
        this.baseMapper.updateAllColumnById(wechatRechargeQrcode);
    }

    @Override
    @Transactional
    public String syncQrcode() {
        this.baseMapper.selectValidForUpdate();
        if (CollectionUtils.isNotEmpty(this.baseMapper.selectUsedQrcodeForUpdate())) {
            throw new RRException("有使用中二维码，暂时无法进行同步");
        }

        this.baseMapper.deleteAllValid();

        // 获取系统文件上传路径
        final String qrcodeRoot = fileUploadProperties.getUploadDir() + "/qrcode";
        final String qrcodeDownRoot = fileUploadProperties.getDownPath() + "/qrcode";
        File qrcodeRootPath = new File(qrcodeRoot);
        if (!qrcodeRootPath.exists()) {
            boolean rs = qrcodeRootPath.mkdirs();
            if (!rs) {
                throw new RRException("同步二维码失败，创建文件目录错误");
            }
        }

        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        List<String> amountList = Optional.ofNullable(qrcodeRootPath.list()).map(Arrays::asList).orElse(new ArrayList<>());
        amountList.forEach(amountStr -> {
            Integer amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException ne) {
                // ignore
                return;
            }

            File amountPath = new File(qrcodeRoot + "/" + amountStr);
            // 是否文件夹
            if (!amountPath.isDirectory()) {
                return;
            }

            List<String> qrcodeList = Optional.ofNullable(amountPath.list()).map(Arrays::asList).orElse(new ArrayList<>());
            AtomicInteger success = new AtomicInteger(0);
            qrcodeList.forEach(qrcodeStr -> {
                String qrcode;
                try {
                    qrcode = Integer.parseInt(qrcodeStr.split("\\.")[0]) + "";
                } catch (Exception e) {
                    // ignore
                    e.printStackTrace();
                    return;
                }

                BigDecimal realAmount = new BigDecimal(amount).add(
                        new BigDecimal(qrcode).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                WechatRechargeQrcode rechargeQrcode = new WechatRechargeQrcode();
                String qrcodePath = qrcodeDownRoot + "/" + amountStr + "/" + qrcodeStr;
                rechargeQrcode.setQrCode(qrcodePath);
                rechargeQrcode.setAmount(amount);
                rechargeQrcode.setRealAmount(realAmount);
                rechargeQrcode.setCreateTime(now);
                this.baseMapper.insert(rechargeQrcode);
                success.addAndGet(1);
            });

            if (success.get() > 0) {
                sb.append(amountStr).append("元充值二维码同步成功").append(success).append("个;<br>");
            }
        });

        return sb.toString();
    }
}
