package com.lontyu.dwjwap.dto.thirdPay;

import com.lontyu.dwjwap.utils.thirdPay.MD5;
import com.lontyu.dwjwap.utils.thirdPay.SHA1;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author as
 * @desc
 * @sinse 2019/4/11
 */
@Data
@ToString
public class BaseRequestDTO {
    private String merchNo;
    private String nonce;
    private Integer timestamp;
    private String data;
    private String sign;

    public BaseRequestDTO() {
    }

    public BaseRequestDTO(String merchNo, String nonce, Integer timestamp, String data) {
        this.merchNo = merchNo;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.data = data;
    }

    // 生成签名
    public BaseRequestDTO generatorSign(String key) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(data)) {
            sb.append("data=").append(this.data).append("&");
        }
        if (StringUtils.isNotEmpty(merchNo)) {
            sb.append("merchNo=").append(this.merchNo).append("&");
        }
        if (StringUtils.isNotEmpty(nonce)) {
            sb.append("nonce=").append(nonce).append("&");
        }
        if (null != timestamp) {
            sb.append("timestamp=").append(timestamp).append("&");
        }
        String params = sb.substring(0, sb.length());
        this.sign = MD5.MD5Encode(SHA1.encrypt(params) .toLowerCase());
        return this;
    }
}
