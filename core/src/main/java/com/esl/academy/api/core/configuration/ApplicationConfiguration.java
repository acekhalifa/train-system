package com.esl.academy.api.core.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class ApplicationConfiguration {

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
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
