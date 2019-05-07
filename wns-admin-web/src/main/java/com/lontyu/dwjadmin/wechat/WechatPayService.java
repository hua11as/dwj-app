package com.lontyu.dwjadmin.wechat;

import com.lontyu.dwjadmin.util.BeanUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/11/28 1:22
 */
@Service
@Slf4j
public class WechatPayService {

    private static final String PAY_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    private static final String APP_ID = "wx96fe4c6ebeada1a6";
    private static final String MCH_ID = "1501698281";
    private static final String KEY = "12345678912345678912345678912345";

    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new SecureRandom();

    @Autowired
    private RestTemplate sslRestTemplate;

    private String getNoceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    private String getSign(PayRequestVO requestVO) {
        Map<String, Object> data = BeanUtils.transBean2Map(requestVO);

        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            if (null != data.get(k)) {// 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).toString().trim()).append("&");
            }
        }
        sb.append("key=").append(KEY);
        System.out.println(sb);
        return MD5(sb.toString()).toUpperCase();
    }

    private String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("utf-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密异常");
        }
    }

    private String getPartnerTradeNo() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public PayResponseVO pay(String openid, Integer amount, String desc, String ip) {
        PayRequestVO requestVO = new PayRequestVO();
        requestVO.setMch_appid(APP_ID);
        requestVO.setMchid(MCH_ID);
        requestVO.setNonce_str(getNoceStr());
        requestVO.setPartner_trade_no(getPartnerTradeNo());
        requestVO.setOpenid(openid);
        requestVO.setCheck_name("NO_CHECK");
        requestVO.setAmount(amount);
        requestVO.setDesc(desc);
        requestVO.setSpbill_create_ip(ip);
        requestVO.setSign(getSign(requestVO));

        try {
            JAXBContext context = JAXBContext.newInstance(PayRequestVO.class, PayResponseVO.class);
            Marshaller marshaller = context.createMarshaller();
            Unmarshaller unmarshaller = context.createUnmarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");  // 设置编码字符集
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化XML输出，有分行和缩进
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            StringWriter sw = new StringWriter();
            marshaller.marshal(requestVO, sw);   // 打印到控制台

            HttpHeaders header = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("text/html; charset=utf-8");
            header.setContentType(type);
            HttpEntity<String> httpEntity = new HttpEntity<>(sw.toString(), header);
            log.info(sw.toString());
            String responseStr = sslRestTemplate.postForObject(PAY_URL, httpEntity, String.class);
            log.info(responseStr);
            return (PayResponseVO) unmarshaller.unmarshal(new StringReader(responseStr));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Data
    @XmlRootElement(name = "xml")
    public static class PayRequestVO {
        private String mch_appid;
        private String mchid;
        private String nonce_str;
        private String sign;
        private String partner_trade_no;
        private String openid;
        private String check_name;
        private Integer amount;
        private String desc;
        private String spbill_create_ip;
    }

    @Data
    @XmlRootElement(name = "xml")
    public static class PayResponseVO {

        private String return_code;
        private String return_msg;

        // when returnCode为'SUCCESS'时
        private String mch_appid;
        private String mchid;
        private String nonce_str;
        private String result_code;
        private String err_code;
        private String err_code_des;

        // when resultCode为'SUCCESS'时
        private String partner_trade_no;
        private String payment_no;
        private String payment_time;

        public boolean isPaySuccess() {
            return "SUCCESS".equals(return_code) && "SUCCESS".equals(result_code);
        }
    }
}
