package com.esl.academy.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
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

	@Bean
	public DelegatingSecurityContextAsyncTaskExecutor taskExecutor() {
		return new DelegatingSecurityContextAsyncTaskExecutor(new SimpleAsyncTaskExecutor());
	}

	@Bean
	public JaroWinklerSimilarity jaroWinklerSimilarity(){
		return new JaroWinklerSimilarity();
	}

	@Bean
	public ObjectMapper objectMapper() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		sdf.setTimeZone(TimeZone.getTimeZone("Africa/Lagos"));
		JavaTimeModule javaTimeModule = new JavaTimeModule();
        Hibernate6Module hibernate6Module = new Hibernate6Module();

		return JsonMapper.builder()
				.addModules(javaTimeModule, hibernate6Module)
				.build().registerModules(javaTimeModule, hibernate6Module)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setDateFormat(sdf);
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}
}
