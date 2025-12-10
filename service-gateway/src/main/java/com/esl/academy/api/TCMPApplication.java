package com.esl.academy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableAsync
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties
@EnableJpaRepositories("com.esl.academy.api")
@EntityScan(basePackages = "com.esl.academy.api")
@SpringBootApplication(scanBasePackages = "com.esl.academy.api")
public class TCMPApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Africa/Lagos"));
		SpringApplication.run(TCMPApplication.class, args);
	}

}
