package com.lontyu;

import com.lontyu.dwjadmin.property.FileUploadProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan(basePackages = {"com.lontyu.dwjadmin.dao"})
@EnableConfigurationProperties(FileUploadProperties.class)
@EnableScheduling
public class DwjAdminApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DwjAdminApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DwjAdminApplication.class);
	}
}
