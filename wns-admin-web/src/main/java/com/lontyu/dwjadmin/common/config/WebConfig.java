package com.lontyu.dwjadmin.common.config;

import com.lontyu.dwjadmin.property.FileUploadProperties;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * WebMvc配置
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Bean
    public RestTemplate restTemplate(){
        return builder.build();
    }

    /**
     * 访问SSL的Template
     *
     * @throws Exception
     */
    @Bean("sslRestTemplate")
    public RestTemplate sslRestTemplate() throws Exception {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .setSSLSocketFactory(getSSLConnectionSocket())
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
        registry.addResourceHandler("/fileStorage/**").addResourceLocations("file:" + fileUploadProperties.getUploadDir());
    }

    private static SSLContext wx_ssl_context = null; //微信支付ssl证书
    private static final String MCH_ID = "1501698281";

    static{
        Resource resource = new ClassPathResource("wx_apiclient_cert.p12");
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] keyPassword = MCH_ID.toCharArray(); //证书密码
            keystore.load(resource.getInputStream(), keyPassword);
            wx_ssl_context = SSLContexts.custom().loadKeyMaterial(keystore, keyPassword).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取ssl connection链接
    private SSLConnectionSocketFactory getSSLConnectionSocket() {
        return new SSLConnectionSocketFactory(wx_ssl_context, new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjemctMapper();
//
//        //生成json时，将所有Long转换成String
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);
//
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        converters.add(0, jackson2HttpMessageConverter);
//    }

}
