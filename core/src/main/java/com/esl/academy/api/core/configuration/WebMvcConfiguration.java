package com.esl.academy.api.core.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${application.cors.liveOrigins:}")
    private String liveOrigins;

    @Value("${application.cors.devOrigins:}")
    private String devOrigins;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(getAllowedOriginPatterns().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(getAllowedOriginPatterns());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        PathPatternParser patternParser = new PathPatternParser();
        configurer.setPatternParser(patternParser);
    }

    private List<String> getAllowedOriginPatterns() {
        final var patterns = new ArrayList<>(Arrays.asList(liveOrigins.split(",")));
        if (!isProduction()) patterns.addAll(Arrays.asList(devOrigins.split(",")));
        return patterns;
    }

    private boolean isProduction() {
        return activeProfiles.equalsIgnoreCase("production");
    }
}
