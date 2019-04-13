package com.lontyu.dwjwap.config;


import com.baidu.fis.servlet.MapListener;
import com.lontyu.dwjwap.config.interceptor.LoginInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig  extends WebMvcConfigurerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    @Bean
    public MapListener mapListener(){
        return new MapListener();
    }

    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.debug("--------------------------------------WebMvcConfig addInterceptors  ");
        registry.addInterceptor(loginInterceptor);
    }
}
