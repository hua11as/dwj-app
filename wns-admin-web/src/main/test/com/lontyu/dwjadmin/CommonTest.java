package com.lontyu.dwjadmin;

import com.lontyu.dwjadmin.dao.SysUserDao;
import com.lontyu.dwjadmin.property.FileUploadProperties;
import com.lontyu.dwjadmin.wechat.WechatPayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/21 22:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class CommonTest {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Autowired
    private WechatPayService wechatPayService;

    @Test
    public void testProperties() {
        System.out.println(fileUploadProperties.getUploadDir());
    }

    @Autowired
    private SysUserDao sysUserDao;

    @Test
    public void mapperTest() {
        sysUserDao.selectById(1);
    }

    @Test
    public void testPay() {
        String openid = "aaa";
        Integer amount = 1;
        String desc = "支付测试";
        String ip = "127.0.0.1";
        wechatPayService.pay(openid, amount, desc, ip);
    }
}
