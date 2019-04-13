package com.lontyu.dwjwap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *  全局参数配置类
 */
@Configuration
public class GlobalsConfig {

    @Value("${weixin.authToken}")
    private String authToken;

    @Value("${weixin.appId}")
    private String appId; // 微信商户appID

    @Value("${weixin.mchId}")
    private String mchId; // 微信商户号

    @Value("${weixin.pay.productDesc}")
    private String productDesc;

    @Value("${weixin.pay.productDetail}")
    private String productDetail;

    @Value("${weixin.key}")
    private String payKey;

    @Value("${weixin.replyContent:欢迎关注，更多功能开发中...}")
    private String replyContent;

    @Value("${weixin.subscrbeContent:欢迎关注，更多功能开发中...}")
    private String subscrbeContent;

    @Value("${weixin.defaultConfigId:1}")
    private Integer defaultConfigId;

    @Value("${dwj.domain}")
    private String domain;

    @Value("${dwj.promotionApi}")
    private String promotionApi;

    @Value("${socketio.server}")
    private String socketServer;

    @Value("${socketio.port}")
    private String socketPort;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getSubscrbeContent() {
        return subscrbeContent;
    }

    public void setSubscrbeContent(String subscrbeContent) {
        this.subscrbeContent = subscrbeContent;
    }

    public Integer getDefaultConfigId() {
        return defaultConfigId;
    }

    public void setDefaultConfigId(Integer defaultConfigId) {
        this.defaultConfigId = defaultConfigId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPromotionApi() {
        return promotionApi;
    }

    public void setPromotionApi(String promotionApi) {
        this.promotionApi = promotionApi;
    }

    public String getSocketServer() {
        return socketServer;
    }

    public void setSocketServer(String socketServer) {
        this.socketServer = socketServer;
    }

    public String getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(String socketPort) {
        this.socketPort = socketPort;
    }
}
