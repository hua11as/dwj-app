package com.lontyu.dwjwap.service;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

/**
 * @decription: TODO
 * @author: as
 * @date: 2018/10/29 0:14
 */
public interface SendSmsService {

    /**
     * 发送短信
     *
     * @param request request
     */
    void sendSms(SendSmsRequest request);

    /**
     * 查询短信结果
     *
     * @param bizId 业务id
     * @return 短信发送结果
     * @throws ClientException 查询异常
     */
    QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException;
}
