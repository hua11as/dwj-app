package com.lontyu.dwjwap;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.utils.NumberUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.lontyu.dwjwap.dao"})
@EnableScheduling
@EnableAsync
@ImportResource({"classpath:application-box.xml"})
public class DwjWapApplication extends SpringBootServletInitializer {

    @Autowired
    private GlobalsConfig globalsConfig;
    public static void main(String[] args) {
        SpringApplication.run(DwjWapApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DwjWapApplication.class);
    }

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(globalsConfig.getSocketServer());
        config.setPort(org.apache.commons.lang.math.NumberUtils.toInt(globalsConfig.getSocketPort(),9092));
//
//        config.setHostname("localhost");
//        config.setPort(9092);
        final SocketIOServer server = new SocketIOServer(config);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }


}